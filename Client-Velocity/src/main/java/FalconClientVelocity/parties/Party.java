package FalconClientVelocity.parties;

import com.velocitypowered.api.proxy.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Party {
    ReentrantLock lock = new ReentrantLock();
    private Player owner;
    private List<Player> members;
    private int limit;

    public static ConcurrentHashMap<UUID, Party> getAllParties(){
        return allParties;
    }

    public Player getOwner() {
        return owner;
    }

    public List<Player> getMembers() {
        return members;
    }

    public static boolean isPlayerInParty(Player p){
        for(Party party : getAllParties().values()){
            if (party.getAllPlayers().contains(p)){
                return true;
            }
        }
        return false;
    }

    public List<Player> getAllPlayers() {
        List<Player> l = new ArrayList<>(members);
        l.add(owner);
        return l;
    }

    public int getLimit() {
        return limit;
    }

    private static ConcurrentHashMap<UUID, Party> allParties = new ConcurrentHashMap<>();

    public Party(Player owner){
        lock.lock();
        try {
            this.owner = owner;
            this.limit = 100;
            members = Collections.synchronizedList(new ArrayList<>());
            allParties.put(owner.getUniqueId(),this);
        }
        finally {
            lock.unlock();
        }
    }
    public Party(Player owner, List<Player> members) {
        lock.lock();
        try {
            this.owner = owner;
            this.limit = 100;
            this.members = Collections.synchronizedList(members);
            allParties.put(owner.getUniqueId(),this);
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
    public void setOwner(Player owner){
        lock.lock();
        try {
            allParties.remove(this.owner.getUniqueId());
            this.owner = owner;
            allParties.put(owner.getUniqueId(), this);
        } finally {
            lock.unlock();
        }
    }
    public void setMembers(List<Player> members) {
        lock.lock();
        try {
            this.members = Collections.synchronizedList(members);
        } finally {
            lock.unlock();
        }
    }
    public void addMembers(Player... members){
        lock.lock();
        try {
            this.members.addAll(Arrays.asList(members));
        } finally {
            lock.unlock();
        }
    }
    public void removeMembers(Player... members){
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
