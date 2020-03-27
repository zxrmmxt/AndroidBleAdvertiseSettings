package com.steelmate.androidbleadvertisesettings;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author xt on 2019/11/20 17:10
 */
public class ParsedAd {
    public Byte       flags;
    public List<UUID> uuids;
    public String     localName;
    public Short      manufacturerId;
    public byte[]     serviceData;
    public byte[]     manufacturerData;

    @Override
    public String toString() {
        return "ParsedAd{" +
                "flags=" + flags +
                ", uuids=" + uuids +
                ", localName='" + localName + '\'' +
                ", manufacturerId=" + manufacturerId +
                ", serviceData=" + Arrays.toString(serviceData) +
                ", manufacturerData=" + Arrays.toString(manufacturerData) +
                '}';
    }
}
