package weblogic.wsee.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import weblogic.apache.xml.serialize.OutputFormat;
import weblogic.apache.xml.serialize.XMLSerializer;
import weblogic.xml.dom.DOMStreamReader;
import weblogic.xml.dom.DOMStreamWriter;
import weblogic.xml.stax.ChildReaderToWriter;

public class UnknownMsgHeader extends MsgHeader implements Externalizable {
    private final MsgHeaderType type = new MsgHeaderType();
    private Element xmlHeader;
    private QName qname;

    public UnknownMsgHeader() {
    }

    public UnknownMsgHeader(QName var1) {
        this.qname = var1;
    }

    public UnknownMsgHeader(Element var1) {
        String var2 = var1.getPrefix();
        if (var2 == null) {
            var2 = "";
        }

        String var3 = var1.getLocalName();
        if (var3 == null) {
            var3 = var1.getNodeName();
        }

        this.qname = new QName(var1.getNamespaceURI(), var3, var2);
        this.xmlHeader = var1;
    }

    public MsgHeaderType getType() {
        return this.type;
    }

    public QName getName() {
        return this.qname;
    }

    public Element getElement() {
        return this.xmlHeader;
    }

    public void read(Element var1) throws MsgHeaderException {
        this.xmlHeader = var1;
    }

    public void write(Element var1) throws MsgHeaderException {
        try {
            if (this.xmlHeader.getLocalName() == null) {
                this.xmlHeader = (Element)var1.getOwnerDocument().importNode(this.xmlHeader, true);
            }

            DOMStreamReader var2 = new DOMStreamReader(this.xmlHeader);
            DOMStreamWriter var3 = new DOMStreamWriter(var1.getOwnerDocument(), var1);
            (new ChildReaderToWriter(var3)).writeChildren(var2);
        } catch (XMLStreamException var4) {
            throw new MsgHeaderException("Could not write " + this.getName().getLocalPart(), var4);
        }
    }

    public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
        String var2 = var1.readUTF();
        String var3 = var1.readUTF();
        String var4 = var1.readUTF();
        this.qname = new QName(var2, var3, var4);
        int var5 = var1.readInt();
        byte[] var6 = new byte[var5];
        boolean var7 = false;

        int var15;
        for(int var8 = 0; var8 < var5; var8 += var15) {
            var15 = var1.read(var6, var8, var5 - var8);
        }

        ByteArrayInputStream var9 = new ByteArrayInputStream(var6);

        try {
            DocumentBuilderFactory var10 = DocumentBuilderFactory.newInstance();
            var10.setNamespaceAware(true);
            DocumentBuilder var11 = var10.newDocumentBuilder();
            Document var12 = var11.parse(var9);
            this.xmlHeader = var12.getDocumentElement();
        } catch (ParserConfigurationException var13) {
            throw new IOException(var13.toString());
        } catch (SAXException var14) {
            throw new IOException(var14.toString());
        }
    }

    public void writeExternal(ObjectOutput var1) throws IOException{
        var1.writeUTF("tr1ple");
        var1.writeUTF("tr1ple");
        var1.writeUTF("tr1ple");
        ByteArrayOutputStream var2 = new ByteArrayOutputStream();
        OutputFormat var3 = new OutputFormat("XML", (String)null, false);
        XMLSerializer var4 = new XMLSerializer(var2, var3);
        Document doc = null;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbBuilder = null;
        try {
            dbBuilder = dbFactory.newDocumentBuilder();
            doc = dbBuilder.parse(System.getProperty("user.dir")+"/src/main/resources/text.xml");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        var4.serialize(doc);
        byte[] var5 = var2.toByteArray();
        var1.writeInt(var5.length);
        var1.write(var5);
    }
}
