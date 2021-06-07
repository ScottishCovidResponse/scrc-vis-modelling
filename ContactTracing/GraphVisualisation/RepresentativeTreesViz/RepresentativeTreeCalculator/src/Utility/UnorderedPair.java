/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author MaxSondag
 */
public class UnorderedPair<X,Y> {

    public final X a;
    public final Y b;

    /**
     * Used for convenience of hashcode and equality checking
     */
    private Set<Object> hashSet = new HashSet();

    public UnorderedPair(X a, Y b) {
        this.a = a;
        this.b = b;
        hashSet.add(a);
        hashSet.add(b);
    }

    @Override
    public int hashCode() {
        return hashSet.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final UnorderedPair<?,?> other = (UnorderedPair<?,?>) obj;
        return this.hashSet.equals(other.hashSet);
    }

}
