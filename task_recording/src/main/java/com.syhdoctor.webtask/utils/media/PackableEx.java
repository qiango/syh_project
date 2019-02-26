package com.syhdoctor.webtask.utils.media;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
