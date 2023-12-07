import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class HashDemo {

    public boolean equals(Object other) {
        //equals 相同，表示两个对象在语义上应该是同一个对象(即使它们在内存里其实是两个对象)，hashcode 应该保证相同
        //hashcode 不同，表示两个对象应该是不同的对象，equals 应该不同
        //hashcode 相同，equals 可能相同，可能不同
        return true;
    }

    public static void main(String[]args) {
        HashDemo d1 = new HashDemo();
        HashDemo d2 = new HashDemo();
        map = new ConcurrentHashMap();
        System.out.println(d1.hashCode() + ", " + d2.hashCode() + d1.equals(d2));
        HashSet<HashDemo> sets = new HashSet<>();
        sets.add(d1);
        sets.add(d2);
        System.out.print(sets.size());
    }

}