/**
 * 
 */
package com.zhisland.data.social.dtos;

/**
 * @author muzongyan
 *
 */
public class UserRelationDto {

    private int fromUid;

    private int toUid;

    /**
     * @return the fromUid
     */
    public int getFromUid() {
        return fromUid;
    }

    /**
     * @param fromUid
     *            the fromUid to set
     */
    public void setFromUid(int fromUid) {
        this.fromUid = fromUid;
    }

    /**
     * @return the toUid
     */
    public int getToUid() {
        return toUid;
    }

    /**
     * @param toUid
     *            the toUid to set
     */
    public void setToUid(int toUid) {
        this.toUid = toUid;
    }

}
