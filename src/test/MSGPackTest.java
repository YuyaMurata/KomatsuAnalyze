/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

/**
 *
 * @author ZZ17390
 */
public class MSGPackTest {

    public static void main(String[] args) throws IOException {
        // Serialize with MessagePacker.
        // MessageBufferPacker is an optimized version of MessagePacker for packing data into a byte array
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        packer
                .packInt(1)
                .packString("leo")
                .packArrayHeader(2)
                .packString("xxx-xxxx")
                .packString("yyy-yyyy");
        packer.close(); // Never forget to close (or flush) the buffer

        // Deserialize with MessageUnpacker
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(packer.toByteArray());
        int id = unpacker.unpackInt();             // 1
        String name = unpacker.unpackString();     // "leo"
        int numPhones = unpacker.unpackArrayHeader();  // 2
        String[] phones = new String[numPhones];
        for (int i = 0; i < numPhones; ++i) {
            phones[i] = unpacker.unpackString();   // phones = {"xxx-xxxx", "yyy-yyyy"}
        }
        unpacker.close();

        System.out.println(String.format("id:%d, name:%s, phone:[%s]", id, name, join(phones)));

    }
    
    private static String join(String[] in)
    {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < in.length; ++i) {
            if (i > 0) {
                s.append(", ");
            }
            s.append(in[i]);
        }
        return s.toString();
    }
}
