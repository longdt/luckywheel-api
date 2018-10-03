package com.foxpify.luckywheel.model.entity;

public class Slide {
    private String label;
    private String discountCode;
    private Long discountCodeId;
    private int probability;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public Long getDiscountCodeId() {
        return discountCodeId;
    }

    public void setDiscountCodeId(Long discountCodeId) {
        this.discountCodeId = discountCodeId;
    }

    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }
}
