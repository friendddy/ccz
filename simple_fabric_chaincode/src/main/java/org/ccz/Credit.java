package org.ccz;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType
public class Credit {
    @Property
    private double credit;

    @Property
    private int nums;

    @Property
    private boolean fail;

    @Property
    private String user;

    public double getCredit() {
        return credit;
    }

    public int getNums() {
        return nums;
    }

    public boolean isFail() {
        return fail;
    }

    public String getUser() {
        return user;
    }

    public Credit(double credit, int nums, boolean fail, String user) {
        this.credit = credit;
        this.nums = nums;
        this.fail = fail;
        this.user = user;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Credit other = (Credit) obj;

        return Objects.deepEquals(new String[] {getUser(), String.valueOf(getCredit()), String.valueOf(getNums()), String.valueOf(getClass())},
                new String[] {other.getUser(), String.valueOf(other.getCredit()), String.valueOf(other.getNums()), String.valueOf(other.getClass())});
    }



}
