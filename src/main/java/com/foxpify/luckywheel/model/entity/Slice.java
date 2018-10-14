package com.foxpify.luckywheel.model.entity;

import com.dslplatform.json.CompiledJson;

@CompiledJson
public class Slice {
    private Integer index;
    private String label;
    private String discountCode;
    private Long priceRuleId;
    private Float probability;
    private boolean auto;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

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

    public Long getPriceRuleId() {
        return priceRuleId;
    }

    public void setPriceRuleId(Long priceRuleId) {
        this.priceRuleId = priceRuleId;
    }

    public Float getProbability() {
        return probability;
    }

    public void setProbability(Float probability) {
        this.probability = probability;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }
}
