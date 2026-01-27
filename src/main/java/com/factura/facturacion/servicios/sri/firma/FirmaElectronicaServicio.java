package com.factura.facturacion.servicios.sri.firma;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Component("firmaRealStrategy")
public class FirmaElectronicaServicio implements FirmaStrategy {

        private static final String ETSI_URI = "http://uri.etsi.org/01903/v1.3.2#";

        @Override
        public byte[] firmar(byte[] xmlBytes, String pathFirma, String claveFirma) throws Exception {
                try {
                        System.out.println(">> INICIO FIRMA ELECTRONICA (v2 Debug)");

                        // 1. Cargar KeyStore y Certificado
                        KeyStore ks = KeyStore.getInstance("PKCS12");
                        try (InputStream is = new FileInputStream(pathFirma)) {
                                ks.load(is, claveFirma.toCharArray());
                        }

                        String alias = ks.aliases().nextElement();
                        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, claveFirma.toCharArray());
                        X509Certificate cert = (X509Certificate) ks.getCertificate(alias);

                        // 2. Parsear XML
                        System.out.println(">> Parseando XML...");
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        dbf.setNamespaceAware(true);
                        dbf.setIgnoringElementContentWhitespace(true);
                        Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xmlBytes));
                        doc.normalizeDocument();

                        // 3. Crear Factory y Contexto
                        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
                        DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());

                        // 4. Crear Reference (firmar todo el documento)
                        List<Transform> transforms = new ArrayList<>();
                        transforms.add(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
                        transforms.add(fac.newTransform(CanonicalizationMethod.INCLUSIVE,
                                        (TransformParameterSpec) null));

                        Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA1, null),
                                        transforms,
                                        null, null);

                        // 5. IDs
                        String signatureId = "Signature-" + UUID.randomUUID().toString();
                        String signedPropsId = "SignedProperties-" + UUID.randomUUID().toString();

                        // 6. Crear SignedInfo
                        // Referencia a SignedProperties (Crucial para XAdES)
                        Reference refXades = fac.newReference("#" + signedPropsId,
                                        fac.newDigestMethod(DigestMethod.SHA1, null),
                                        Collections.singletonList(
                                                        fac.newTransform(CanonicalizationMethod.INCLUSIVE,
                                                                        (TransformParameterSpec) null)),
                                        "http://uri.etsi.org/01903#SignedProperties", null);

                        List<Reference> references = new ArrayList<>();
                        references.add(ref);
                        references.add(refXades);

                        SignedInfo si = fac.newSignedInfo(
                                        fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
                                                        (C14NMethodParameterSpec) null),
                                        fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                                        references);

                        // 7. Crear KeyInfo
                        KeyInfoFactory kif = fac.getKeyInfoFactory();
                        List<Object> x509Content = new ArrayList<>();
                        x509Content.add(cert);
                        X509Data xd = kif.newX509Data(x509Content);
                        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

                        // 8. Estructura XAdES-BES
                        System.out.println(">> Creando XAdES...");
                        Element qualifyingProperties = createQualifyingProperties(doc, cert, "#" + signatureId,
                                        signedPropsId);

                        XMLObject xadesObject = fac.newXMLObject(
                                        Collections.singletonList(
                                                        new javax.xml.crypto.dom.DOMStructure(qualifyingProperties)),
                                        null, null,
                                        null);

                        // 9. Registrar ID explícitamente en el Contexto (FIX 500 Error)
                        // Buscamos el elemento y le decimos al contexto: "Oye, este atributo 'Id' es un
                        // ID"
                        Element signedPropertiesElem = (Element) qualifyingProperties
                                        .getElementsByTagNameNS(ETSI_URI, "SignedProperties").item(0);

                        if (signedPropertiesElem == null)
                                throw new RuntimeException("SignedProperties no encontrado en DOM generado");

                        // IMPORTANTÍSIMO: Registrar el elemento ID en el contexto ANTES de firmar
                        dsc.setIdAttributeNS(signedPropertiesElem, null, "Id");

                        // 10. Firmar
                        System.out.println(">> Firmando...");
                        List<XMLObject> objects = Collections.singletonList(xadesObject);
                        XMLSignature signature = fac.newXMLSignature(si, ki, objects, signatureId, null);

                        signature.sign(dsc);
                        System.out.println(">> Firma completada exitosamente.");

                        // 11. Retornar bytes
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        TransformerFactory tf = TransformerFactory.newInstance();
                        Transformer trans = tf.newTransformer();
                        trans.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");
                        trans.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "no");
                        trans.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "no");
                        trans.transform(new DOMSource(doc), new StreamResult(os));

                        return os.toByteArray();

                } catch (Exception e) {
                        System.err.println(">>>> ERROR FATAL EN FIRMA ELECTRONICA <<<<");
                        e.printStackTrace(); // Esto saldrá en la consola del usuario
                        throw e; // Re-lanzar para que salga 500 y no oculte el error
                }
        }

        private Element createQualifyingProperties(Document doc, X509Certificate cert, String targetSignatureId,
                        String signedPropsId)
                        throws Exception {
                Element qualifyingProperties = doc.createElementNS(ETSI_URI, "etsi:QualifyingProperties");
                qualifyingProperties.setAttribute("Target", targetSignatureId);

                Element signedProperties = doc.createElementNS(ETSI_URI, "etsi:SignedProperties");
                signedProperties.setAttribute("Id", signedPropsId);
                qualifyingProperties.appendChild(signedProperties);

                Element signedSignatureProperties = doc.createElementNS(ETSI_URI, "etsi:SignedSignatureProperties");
                signedProperties.appendChild(signedSignatureProperties);

                // SigningTime
                Element signingTime = doc.createElementNS(ETSI_URI, "etsi:SigningTime");
                signingTime.setTextContent(
                                java.time.ZonedDateTime.now()
                                                .format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                signedSignatureProperties.appendChild(signingTime);

                // SigningCertificate
                Element signingCertificate = doc.createElementNS(ETSI_URI, "etsi:SigningCertificate");
                signedSignatureProperties.appendChild(signingCertificate);

                Element certTag = doc.createElementNS(ETSI_URI, "etsi:Cert");
                signingCertificate.appendChild(certTag);

                Element certDigest = doc.createElementNS(ETSI_URI, "etsi:CertDigest");
                certTag.appendChild(certDigest);

                Element digestMethod = doc.createElementNS(ETSI_URI, "etsi:DigestMethod");
                digestMethod.setAttribute("Algorithm", "http://www.w3.org/2000/09/xmldsig#sha1");
                certDigest.appendChild(digestMethod);

                Element digestValue = doc.createElementNS(ETSI_URI, "etsi:DigestValue");
                // Calcular SHA1 del certificado
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                byte[] hash = md.digest(cert.getEncoded());
                digestValue.setTextContent(java.util.Base64.getEncoder().encodeToString(hash));
                certDigest.appendChild(digestValue);

                Element issuerSerial = doc.createElementNS(ETSI_URI, "etsi:IssuerSerial");
                certTag.appendChild(issuerSerial);

                Element x509IssuerName = doc.createElementNS(ETSI_URI, "etsi:X509IssuerName");
                x509IssuerName.setTextContent(cert.getIssuerX500Principal().getName());
                issuerSerial.appendChild(x509IssuerName);

                Element x509SerialNumber = doc.createElementNS(ETSI_URI, "etsi:X509SerialNumber");
                x509SerialNumber.setTextContent(cert.getSerialNumber().toString());
                issuerSerial.appendChild(x509SerialNumber);

                return qualifyingProperties;
        }
}
