package dev.mrflyn.veclinkcommon;

public class PacketFormat {

    private final Class[] types;

    public PacketFormat(Class... types){
        this.types = types;
    }

    public Class[] getTypes() {
        return types;
    }
}
