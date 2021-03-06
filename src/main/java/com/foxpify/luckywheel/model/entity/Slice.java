package com.foxpify.luckywheel.model.entity;

import com.dslplatform.json.CompiledJson;

@CompiledJson
public class Slice {
    private Integer index;
    private String label;
    private String discountCode;
    private Long priceRuleId;
    private Float probability;
    private Boolean auto;

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

    public Boolean getAuto() {
        return auto;
    }

    public void setAuto(Boolean auto) {
        this.auto = auto;
    }
}
