package dev.mrflyn.veclinkserver.parties;

import dev.mrflyn.veclinkcommon.VLPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Party {
    ReentrantLock lock = new ReentrantLock();
    private VLPlayer owner;
    private List<VLPlayer> members;
    private int limit;

    public static ConcurrentHashMap<VLPlayer, Party> getAllParties(){
        return allParties;
    }

    public VLPlayer getOwner() {
        return owner;
    }

    public List<VLPlayer> getMembers() {
        return members;
    }

    public List<VLPlayer> getAllPlayers() {
        List<VLPlayer> l = new ArrayList<>(members);
        l.add(owner);
        return l;
    }

    public int getLimit() {
        return limit;
    }

    private static ConcurrentHashMap<VLPlayer, Party> allParties = new ConcurrentHashMap<>();

    public Party (VLPlayer owner){
        lock.lock();
        try {
            this.owner = owner;
            this.limit = 100;
            members = Collections.synchronizedList(new ArrayList<>());
            allParties.put(owner,this);
        }
        finally {
            lock.unlock();
        }
    }
    public Party(VLPlayer owner, List<VLPlayer> members) {
        lock.lock();
        try {
            this.owner = owner;
            this.limit = 100;
            this.members = Collections.synchronizedList(members);
            allParties.put(owner,this);
        } finally {
            lock.unlock();
        }
    }
    public void setLimit(int i){
        lock.lock();
        try {
            this.limit = i;
        } finally {
            lock.unlock();
        }
    }
    public void setOwner(VLPlayer owner){
        lock.lock();
        try {
            allParties.remove(this.owner);
            this.owner = owner;
            allParties.put(owner, this);
        } finally {
            lock.unlock();
        }
    }
    public void setMembers(List<VLPlayer> members) {
        lock.lock();
        try {
            this.members = Collections.synchronizedList(members);
        } finally {
            lock.unlock();
        }
    }
    public void addMembers(VLPlayer... members){
        lock.lock();
        try {
            this.members.addAll(Arrays.asList(members));
        } finally {
            lock.unlock();
        }
    }
    public void removeMembers(VLPlayer... members){
        lock.lock();
        try {
            this.members.removeAll(Arrays.asList(members));
        } finally {
            lock.unlock();
        }
    }
    public void clearMembers(){
        lock.lock();
        try {
            this.members.clear();
        } finally {
            lock.unlock();
        }
    }





}
