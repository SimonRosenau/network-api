import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * Author: Simon Rosenau
 * Date: 11.08.2019
 * Time: 13:11
 */

public class ByteBufTest {

    @Test
    public void test() {
        String string = "Test String";
        ByteBuf buffer = Unpooled.buffer();
        ByteBuf second = Unpooled.buffer();

        buffer.writeBytes(string.getBytes());
        second.writeBytes(buffer.readBytes(buffer.readableBytes()));
        buffer.discardReadBytes();

        byte[] bytes = new byte[second.readableBytes()];
        second.readBytes(bytes);
        assertEquals(string, new String(bytes));
    }

}
