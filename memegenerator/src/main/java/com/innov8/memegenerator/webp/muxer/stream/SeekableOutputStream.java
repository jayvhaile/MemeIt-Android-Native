package com.innov8.memegenerator.webp.muxer.stream;

import java.io.IOException;

/**
 * @author Bartlomiej Tadych, b4rtaz
 */
public interface SeekableOutputStream {

    void setPosition(int position) throws IOException;
    void write(byte[] bytes, int length) throws IOException;
    void close() throws IOException;
}
