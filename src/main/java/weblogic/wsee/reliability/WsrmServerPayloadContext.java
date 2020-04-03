package weblogic.wsee.reliability;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import weblogic.apache.xml.serialize.OutputFormat;
import weblogic.apache.xml.serialize.XMLSerializer;
import weblogic.wsee.addressing.EndpointReference;
import weblogic.wsee.util.Verbose;

public class WsrmServerPayloadContext extends WsrmPayloadContext {
    private static final boolean verbose = Verbose.isVerbose(WsrmServerPayloadContext.class);
    private EndpointReference fromEndpt;
    private EndpointReference faultToEndpt;

    public WsrmServerPayloadContext() {
    }

    public void setFaultTo(EndpointReference var1) {
        this.faultToEndpt = var1;
    }

    public void setFrom(EndpointReference var1) {
        this.fromEndpt = var1;
    }

    public EndpointReference getFaultTo() {
        return this.faultToEndpt;
    }

    public EndpointReference getFrom() {
        return this.fromEndpt;
    }

    @Override
    public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
        super.readExternal(var1);
        int var2 = var1.readInt();
        if (var2 > 0) {
            this.fromEndpt = this.readEndpt(var1, var2);
        } else {
            this.fromEndpt = null;
        }

        var2 = var1.readInt();
        if (var2 > 0) {
            this.faultToEndpt = this.readEndpt(var1, var2);
        } else {
            this.faultToEndpt = null;
        }

    }

    private EndpointReference readEndpt(ObjectInput var1, int var2) throws IOException, ClassNotFoundException {
        byte[] var3 = new byte[var2];
        boolean var4 = false;

        int var13;
        for(int var5 = 0; var5 < var2; var5 += var13) {
            var13 = var1.read(var3, var5, var2 - var5);
        }

        if (verbose) {
            Verbose.log("Reading Endpoint:");

            for(int var6 = 0; var6 < var3.length; ++var6) {
                Verbose.getOut().print((char)var3[var6]);
            }

            Verbose.getOut().println();
        }

        ByteArrayInputStream var14 = new ByteArrayInputStream(var3);

        try {
            DocumentBuilderFactory var7 = DocumentBuilderFactory.newInstance();
            var7.setNamespaceAware(true);
            DocumentBuilder var8 = var7.newDocumentBuilder();
            Document var9 = var8.parse(var14);
            EndpointReference var10 = new EndpointReference();
            var10.read(var9.getDocumentElement());
            return var10;
        } catch (ParserConfigurationException var11) {
            throw new IOException(var11.toString());
        } catch (SAXException var12) {
            throw new IOException(var12.toString());
        }
    }

    @Override
    public void writeExternal(ObjectOutput var1) throws IOException {
        super.writeExternal(var1);
        if (this.fromEndpt != null) {
            try {
                this.writeEndpt(this.fromEndpt, var1);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        } else {
            var1.writeInt(0);
        }

        if (this.faultToEndpt != null) {
            try {
                this.writeEndpt(this.faultToEndpt, var1);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        } else {
            var1.writeInt(0);
        }

    }

    private void writeEndpt(EndpointReference var1, ObjectOutput var2) throws IOException, ParserConfigurationException, SAXException {
        ByteArrayOutputStream var3 = new ByteArrayOutputStream();
        OutputFormat var4 = new OutputFormat("XML", (String)null, false);
        XMLSerializer var5 = new XMLSerializer(var3, var4);
        Document doc = null;
        Element element = null;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
        doc = dbBuilder.parse(System.getProperty("user.dir")+"/src/main/resources/text.xml");
        var5.serialize(doc);
        byte[] var6 = var3.toByteArray();
        if (verbose) {
            Verbose.log("Writing Endpoint:");

            for(int var7 = 0; var7 < var6.length; ++var7) {
                Verbose.getOut().print((char)var6[var7]);
            }

            Verbose.getOut().println();
        }

        var2.writeInt(var6.length);
        var2.write(var6);
    }
}

