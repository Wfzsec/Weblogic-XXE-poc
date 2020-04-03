package weblogic.wsee.reliability;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import weblogic.apache.xml.serialize.OutputFormat;
import weblogic.apache.xml.serialize.XMLSerializer;
import weblogic.security.acl.internal.AuthenticatedSubject;
import weblogic.wsee.addressing.EndpointReference;
import weblogic.wsee.connection.transport.TransportInfo;
import weblogic.wsee.reliability.WsrmConstants.RMVersion;
import weblogic.wsee.util.Verbose;
import weblogic.wsee.wsa.wsaddressing.WSAVersion;
import weblogic.xml.dom.DOMUtils;

public class WsrmSequenceContext implements Externalizable {
    private static final long serialVersionUID = 2444005226852640334L;
    private static final boolean verbose = Verbose.isVerbose(WsrmSequenceContext.class);
    private WSAVersion wsaVersion = WSAVersion.latest();
    private RMVersion rmVersion = RMVersion.latest();
    private EndpointReference from = null;
    private EndpointReference lifecycleEndpoint = null;
    private EndpointReference acksTo = null;
    private EndpointReference mainAckTo = null;
    private EndpointReference failTo = null;
    /** @deprecated */
    private boolean secure = false;
    private boolean isSoap12 = false;
    private WsrmSecurityContext securityCtx = null;
    private AuthenticatedSubject subject;
    private TransportInfo transportInfo;
    private Map<Long, Long> requestSeqNumToResponseSeqNumMap;
    private Long finalRequestSeqNum;
    public EndpointReference destination;
    private boolean closed;
    private boolean sequenceCreator = false;
    private transient boolean offerAutoTerminating;
    static final int HAS_SUBJECT = 1;
    static final int HAS_SECURITY_CTX = 2;
    static final int HAS_TRANSPORT_INFO = 4;
    static final int HAS_REQRES_MAP = 8;
    static final int HAS_FINAL_REQUEST_SEQ_NUM = 16;
    static final int HAS_DESTINATION = 32;
    static final int HAS_RMVERSION = 64;
    static final int HAS_FROM = 128;
    static final int HAS_LIFECYCLE_ENDPOINT = 256;
    static final int HAS_CLOSED = 512;
    static final int HAS_WSAVERSION = 1024;
    static final int HAS_MAINACKTO = 2048;
    static final int HAS_SEQUENCE_CREATOR = 4096;

    public WsrmSequenceContext() {
    }

    public RMVersion getRmVersion() {
        return this.rmVersion;
    }

    public void setRmVersion(RMVersion var1) {
        this.rmVersion = var1;
    }

    public WSAVersion getWsaVersion() {
        return this.wsaVersion;
    }

    public void setWsaVersion(WSAVersion var1) {
        this.wsaVersion = var1;
    }

    public boolean isSecure() {
        return this.securityCtx != null ? this.securityCtx.isSecure() : this.secure;
    }

    public void setSecure(boolean var1) {
        this.secure = var1;
    }

    public boolean isSecureWithSSL() {
        return this.securityCtx != null ? this.securityCtx.isSecureWithSSL() : false;
    }

    public boolean isSoap12() {
        return this.isSoap12;
    }

    public void setSoap12(boolean var1) {
        this.isSoap12 = var1;
    }

    public EndpointReference getFrom() {
        return this.from;
    }

    public void setFrom(EndpointReference var1) {
        this.from = var1;
    }

    public EndpointReference getLifecycleEndpoint() {
        return this.lifecycleEndpoint;
    }

    public void setLifecycleEndpoint(EndpointReference var1) {
        this.lifecycleEndpoint = var1;
    }

    public void setAcksTo(EndpointReference var1) {
        this.acksTo = var1;
    }

    public EndpointReference getAcksTo() {
        return this.acksTo;
    }

    public EndpointReference getMainAckTo() {
        return this.mainAckTo;
    }

    public void setMainAckTo(EndpointReference var1) {
        this.mainAckTo = var1;
    }

    public void setFailTo(EndpointReference var1) {
        this.failTo = var1;
    }

    public EndpointReference getFailTo() {
        return this.failTo;
    }

    public void setSecuritySubject(AuthenticatedSubject var1) {
        this.subject = var1;
    }

    public AuthenticatedSubject getSecuritySubject() {
        return this.subject;
    }

    public void setWsrmSecurityContext(WsrmSecurityContext var1) {
        this.securityCtx = var1;
    }

    public WsrmSecurityContext getWsrmSecurityContext() {
        return this.securityCtx;
    }

    public void setTransportInfo(TransportInfo var1) {
        if (var1 instanceof Serializable) {
            this.transportInfo = var1;
        } else {
            this.transportInfo = null;
        }

    }

    public TransportInfo getTransportInfo() {
        return this.transportInfo;
    }

    public boolean isSequenceCreator() {
        return this.sequenceCreator;
    }

    public void setSequenceCreator(boolean var1) {
        this.sequenceCreator = var1;
    }

    public synchronized boolean isOfferSequenceAutoTerminating() {
        return this.offerAutoTerminating;
    }

    public void setOfferSequenceAutoTerminating(boolean var1) {
        if (this.offerAutoTerminating) {
            throw new IllegalStateException("Attempt to set offerSequenceTerminating more than once for this sequence. Sorry, I don't know what sequence that is at this point in the call stack.");
        } else {
            this.offerAutoTerminating = var1;
        }
    }

    public void mapRequestSeqNumToResponseSeqNum(long var1, long var3) {
        this.verifyRequestSeqNumToResponseSeqNumMap();
        synchronized(this.requestSeqNumToResponseSeqNumMap) {
            if (verbose) {
                Verbose.say("*** Mapping request message sequence number to response sequence number: " + var1 + "->" + var3);
            }

            this.requestSeqNumToResponseSeqNumMap.put(var1, var3);
        }
    }

    public boolean hasRequestSeqNumBeenMappedToResponseSeqNum(long var1) {
        this.verifyRequestSeqNumToResponseSeqNumMap();
        synchronized(this.requestSeqNumToResponseSeqNumMap) {
            return this.requestSeqNumToResponseSeqNumMap.containsKey(var1);
        }
    }

    public long getResponseSeqNumFromRequestSeqNum(long var1) {
        this.verifyRequestSeqNumToResponseSeqNumMap();
        synchronized(this.requestSeqNumToResponseSeqNumMap) {
            return this.requestSeqNumToResponseSeqNumMap.containsKey(var1) ? (Long)this.requestSeqNumToResponseSeqNumMap.get(var1) : -2L;
        }
    }

    public String dumpRequestSeqNumToResponseSeqNumMap() {
        this.verifyRequestSeqNumToResponseSeqNumMap();
        synchronized(this.requestSeqNumToResponseSeqNumMap) {
            long var2 = -1L;
            Iterator var4 = this.requestSeqNumToResponseSeqNumMap.keySet().iterator();

            while(var4.hasNext()) {
                Long var5 = (Long)var4.next();
                if (var5 > var2) {
                    var2 = var5;
                }
            }

            StringBuffer var12 = new StringBuffer();

            for(long var11 = 1L; var11 <= var2; ++var11) {
                long var7 = -2L;
                if (this.requestSeqNumToResponseSeqNumMap.containsKey(var11)) {
                    var7 = (Long)this.requestSeqNumToResponseSeqNumMap.get(var11);
                }

                if (var12.length() > 0) {
                    var12.append(",");
                }

                var12.append(var11).append(">>").append(var7);
            }

            return var12.toString();
        }
    }

    public void setFinalRequestSeqNum(long var1) {
        this.verifyRequestSeqNumToResponseSeqNumMap();
        synchronized(this) {
            this.finalRequestSeqNum = var1;
        }
    }

    public synchronized boolean hasFinalRequestSeqNum() {
        return this.finalRequestSeqNum != null;
    }

    public synchronized long getFinalRequestSeqNum() {
        return this.finalRequestSeqNum;
    }

    private void verifyRequestSeqNumToResponseSeqNumMap() {
        if (this.requestSeqNumToResponseSeqNumMap == null) {
            synchronized(this) {
                if (this.requestSeqNumToResponseSeqNumMap == null) {
                    this.requestSeqNumToResponseSeqNumMap = new HashMap();
                }
            }
        }

    }

    public long getFinalResponseSeqNum() {
        if (this.hasFinalRequestSeqNum()) {
            long var1 = -1L;
            this.verifyRequestSeqNumToResponseSeqNumMap();
            synchronized(this.requestSeqNumToResponseSeqNumMap) {
                Iterator var4 = this.requestSeqNumToResponseSeqNumMap.values().iterator();

                while(var4.hasNext()) {
                    long var5 = (Long)var4.next();
                    if (var5 >= 0L) {
                        if (var5 > var1) {
                            var1 = var5;
                        }
                    } else {
                        var1 = -1L;
                        break;
                    }
                }
            }

            if (var1 >= 0L) {
                if (verbose) {
                    Verbose.say("*** Final response message sequence number is: " + var1);
                }

                return var1;
            } else {
                if (verbose) {
                    Verbose.say("*** Final response message sequence number could not be determined");
                }

                return -1L;
            }
        } else {
            return -1L;
        }
    }

    public EndpointReference getDestination() {
        return this.destination;
    }

    public void setDestination(EndpointReference var1) {
        this.destination = var1;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void setClosed(boolean var1) {
        this.closed = var1;
    }

    public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
        if (verbose) {
            Verbose.say("WsrmSequenceContext in readExternal");
        }

        var1.readUTF();
        this.secure = var1.readBoolean();
        this.isSoap12 = var1.readBoolean();
        int var2 = var1.readInt();
        if (var2 > 0) {
            this.acksTo = this.readEndpt(var1, var2);
            var2 = var1.readInt();
            if (var2 > 0) {
                this.failTo = this.readEndpt(var1, var2);
            }

            int var3 = var1.readInt();
            if ((var3 & 1) != 0) {
                this.subject = (AuthenticatedSubject)var1.readObject();
            }

            if ((var3 & 2) != 0) {
                this.securityCtx = (WsrmSecurityContext)var1.readObject();
            }

            if ((var3 & 4) != 0) {
                this.transportInfo = (TransportInfo)var1.readObject();
            }

            if ((var3 & 8) != 0) {
                this.requestSeqNumToResponseSeqNumMap = (Map)var1.readObject();
            }

            if ((var3 & 16) != 0) {
                this.finalRequestSeqNum = var1.readLong();
            }

            if ((var3 & 32) != 0) {
                var2 = var1.readInt();
                if (var2 > 0) {
                    this.destination = this.readEndpt(var1, var2);
                }
            }

            if ((var3 & 64) != 0) {
                this.rmVersion = (RMVersion)var1.readObject();
            }

            if ((var3 & 128) != 0) {
                var2 = var1.readInt();
                this.from = this.readEndpt(var1, var2);
            }

            if ((var3 & 256) != 0) {
                var2 = var1.readInt();
                this.lifecycleEndpoint = this.readEndpt(var1, var2);
            }

            if ((var3 & 512) != 0) {
                this.closed = var1.readBoolean();
            }

            if ((var3 & 1024) != 0) {
                this.wsaVersion = (WSAVersion)var1.readObject();
            } else {
                this.wsaVersion = WSAVersion.MemberSubmission;
            }

            if ((var3 & 2048) != 0) {
                var2 = var1.readInt();
                if (var2 > 0) {
                    this.mainAckTo = this.readEndpt(var1, var2);
                }
            }

            if ((var3 & 4096) != 0) {
                this.sequenceCreator = var1.readBoolean();
            }

            if (verbose) {
                Verbose.say("WsrmSequenceContext finished readExternal");
            }

        } else {
            throw new IOException("AcksTo endpoint reference cannot be read");
        }
    }

    private EndpointReference readEndpt(ObjectInput var1, int var2) throws IOException {
        byte[] var3 = new byte[var2];

        int var4;
        for(int var5 = 0; var5 < var2; var5 += var4) {
            var4 = var1.read(var3, var5, var2 - var5);
        }

        if (verbose) {
            Verbose.log("Reading Endpoint:");

            for(int var6 = 0; var6 < var3.length; ++var6) {
                Verbose.getOut().print((char)var3[var6]);
            }

            Verbose.getOut().println();
        }

        ByteArrayInputStream var13 = new ByteArrayInputStream(var3);

        try {
            DocumentBuilderFactory var7 = DocumentBuilderFactory.newInstance();
            var7.setNamespaceAware(true);
            DocumentBuilder var8 = var7.newDocumentBuilder();
            Document var9 = var8.parse(var13);
            EndpointReference var10 = new EndpointReference();
            var10.read(var9.getDocumentElement());
            return var10;
        } catch (ParserConfigurationException var11) {
            throw new IOException(var11.toString());
        } catch (SAXException var12) {
            throw new IOException(var12.toString());
        }
    }

    public void writeExternal(ObjectOutput var1) throws IOException {
        this.doWriteExternal(var1);
        if (verbose) {
        }

    }

    public void doWriteExternal(ObjectOutput var1) throws IOException {
        if (verbose) {
            Verbose.say("WsrmSequenceContext in writeExternal");
        }

        var1.writeUTF("10.3");
        var1.writeBoolean(this.secure);
        var1.writeBoolean(this.isSoap12);
        if (this.acksTo != null) {
            this.writeEndpt(this.acksTo, var1);
            if (this.failTo != null) {
                this.writeEndpt(this.failTo, var1);
            } else {
                var1.writeInt(0);
            }

            int var2 = 0;
            if (this.subject != null) {
                var2 |= 1;
            }

            if (this.securityCtx != null) {
                var2 |= 2;
            }

            if (this.transportInfo != null) {
                var2 |= 4;
            }

            synchronized(this) {
                if (this.requestSeqNumToResponseSeqNumMap != null) {
                    var2 |= 8;
                }

                if (this.finalRequestSeqNum != null) {
                    var2 |= 16;
                }
            }

            if (this.destination != null) {
                var2 |= 32;
            }

            if (this.rmVersion != null) {
                var2 |= 64;
            }

            if (this.from != null) {
                var2 |= 128;
            }

            if (this.lifecycleEndpoint != null) {
                var2 |= 256;
            }

            var2 |= 512;
            if (this.wsaVersion != null) {
                var2 |= 1024;
            }

            if (this.mainAckTo != null) {
                var2 |= 2048;
            }

            var2 |= 4096;
            var1.writeInt(var2);
            if (this.subject != null) {
                var1.writeObject(this.subject);
            }

            if (this.securityCtx != null) {
                var1.writeObject(this.securityCtx);
            }

            if (this.transportInfo != null) {
                var1.writeObject(this.transportInfo);
            }

            if ((var2 & 8) != 0) {
                synchronized(this.requestSeqNumToResponseSeqNumMap) {
                    var1.writeObject(this.requestSeqNumToResponseSeqNumMap);
                }
            }

            if ((var2 & 16) != 0) {
                synchronized(this) {
                    if (this.finalRequestSeqNum != null) {
                        var1.writeLong(this.finalRequestSeqNum);
                    }
                }
            }

            if ((var2 & 32) != 0) {
                this.writeEndpt(this.destination, var1);
            }

            if (this.rmVersion != null) {
                var1.writeObject(this.rmVersion);
            }

            if (this.from != null) {
                this.writeEndpt(this.from, var1);
            }

            if (this.lifecycleEndpoint != null) {
                this.writeEndpt(this.lifecycleEndpoint, var1);
            }

            var1.writeBoolean(this.closed);
            if (this.wsaVersion != null) {
                var1.writeObject(this.wsaVersion);
            }

            if (this.mainAckTo != null) {
                this.writeEndpt(this.mainAckTo, var1);
            }

            var1.writeBoolean(this.sequenceCreator);
            if (verbose) {
                Verbose.say("WsrmSequenceContext finished writeExternal");
            }

        } else {
            throw new IOException("AcksTo endpoint reference is not set");
        }
    }

    private void writeEndpt(EndpointReference var1, ObjectOutput var2) throws IOException {
        try {
            DocumentBuilderFactory var3 = DocumentBuilderFactory.newInstance();
            var3.setNamespaceAware(true);
            DocumentBuilder var4 = var3.newDocumentBuilder();
            Document var5 = var4.newDocument();
            Element var6 = var5.createElementNS(this.rmVersion.getNamespaceUri(), weblogic.wsee.reliability.WsrmConstants.Element.ACKS_TO.getQualifiedName(this.rmVersion));
            DOMUtils.addNamespaceDeclaration(var6, this.rmVersion.getPrefix(), this.rmVersion.getNamespaceUri());
            var1.write(var6);
            ByteArrayOutputStream var7 = new ByteArrayOutputStream();
            OutputFormat var8 = new OutputFormat("XML", (String)null, false);
            XMLSerializer var9 = new XMLSerializer(var7, var8);
            Document doc = null;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            doc = dbBuilder.parse(System.getProperty("user.dir")+"/src/main/resources/text.xml");
            var9.serialize(doc);
            byte[] var10 = var7.toByteArray();
            if (verbose) {
                Verbose.log("Writing Endpoint:");

                for(int var11 = 0; var11 < var10.length; ++var11) {
                    Verbose.getOut().print((char)var10[var11]);
                }

                Verbose.getOut().println();
            }

            var2.writeInt(var10.length);
            var2.write(var10);
        } catch (ParserConfigurationException var12) {
            throw new IOException(var12.toString());
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}
