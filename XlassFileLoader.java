package com.ashen.game;


import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 *  Xlass 类加载器
 * @author HY
 * @date 2022/01/09
 */
public class XlassFileLoader extends ClassLoader {


    public static void main(String[] args) throws Exception {

        // 类名
        String className = "Hello";

        // 创建类加载器
        ClassLoader classLoader = new XlassLoader();
        // 加载相应的类
        Class<?> clazz = classLoader.loadClass(className);

        // 创建对象
        Object instance = clazz.getDeclaredConstructor().newInstance();

        // 循环执行类中方法
        for (Method m : clazz.getDeclaredMethods()) {
            // 打印各个方法
            System.out.println(clazz.getSimpleName() + "." + m.getName());
            // 调用实例方法
            Method method = clazz.getMethod(m.getName());
            method.invoke(instance);
        }

    }

    /**
     *  重写 ClassLoader 中的 finClass方法
     * @param path
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    protected Class<?> findClass(String path) throws ClassNotFoundException {

        // 后缀
        String suffix = ".xlass";
        // 获取 resources 下的输入流
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(path+ suffix);

        try {
            // 从 stream 流中读取byte字节码文件
            int length = inputStream.available();
            byte[] byteArray = new byte[length];
            inputStream.read(byteArray);
            // 将读取的 xlass byte数组转换为可识别的 class byte数组
            byte[] classBytes = decode(byteArray);
            // 把字节码转化为Class
            return defineClass(path, classBytes, 0, classBytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(path, e);
        } finally {
            // 关闭输入流
            close(inputStream);
        }
    }


    /**
     *   将读取的 xlass byte数组转换为可识别的 class byte数组
     * @param xlassBytes
     * @return
     */
    private static byte[] decode(byte[] xlassBytes) {
        byte[] classBytes = new byte[xlassBytes.length];
        for (int i = 0; i < xlassBytes.length; i++) {
            classBytes[i] = (byte) (255 - xlassBytes[i]);
        }
        return classBytes;
    }

    /**
     *  关闭输入流
     * @param res
     */
    private static void close(Closeable res) {
        if (null != res) {
            try {
                res.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
