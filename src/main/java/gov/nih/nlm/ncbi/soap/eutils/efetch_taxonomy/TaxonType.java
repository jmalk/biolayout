
package gov.nih.nlm.ncbi.soap.eutils.efetch_taxonomy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TaxonType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TaxonType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}TaxId"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}ScientificName"/>
 *         &lt;element name="OtherNames" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}OtherNamesType" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}ParentTaxId" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}Rank" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}Division" minOccurs="0"/>
 *         &lt;element name="GeneticCode" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}GeneticCodeType" minOccurs="0"/>
 *         &lt;element name="MitoGeneticCode" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}MitoGeneticCodeType" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}Lineage" minOccurs="0"/>
 *         &lt;element name="LineageEx" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}LineageExType" minOccurs="0"/>
 *         &lt;element name="Citations" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}CitationsType" minOccurs="0"/>
 *         &lt;element name="Modifiers" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}ModifiersType" minOccurs="0"/>
 *         &lt;element name="Properties" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}PropertiesType" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}CreateDate" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}UpdateDate" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}PubDate" minOccurs="0"/>
 *         &lt;element name="AkaTaxIds" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}AkaTaxIdsType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaxonType", propOrder = {
    "taxId",
    "scientificName",
    "otherNames",
    "parentTaxId",
    "rank",
    "division",
    "geneticCode",
    "mitoGeneticCode",
    "lineage",
    "lineageEx",
    "citations",
    "modifiers",
    "properties",
    "createDate",
    "updateDate",
    "pubDate",
    "akaTaxIds"
})
public class TaxonType {

    @XmlElement(name = "TaxId", required = true)
    protected String taxId;
    @XmlElement(name = "ScientificName", required = true)
    protected String scientificName;
    @XmlElement(name = "OtherNames")
    protected OtherNamesType otherNames;
    @XmlElement(name = "ParentTaxId")
    protected String parentTaxId;
    @XmlElement(name = "Rank")
    protected String rank;
    @XmlElement(name = "Division")
    protected String division;
    @XmlElement(name = "GeneticCode")
    protected GeneticCodeType geneticCode;
    @XmlElement(name = "MitoGeneticCode")
    protected MitoGeneticCodeType mitoGeneticCode;
    @XmlElement(name = "Lineage")
    protected String lineage;
    @XmlElement(name = "LineageEx")
    protected LineageExType lineageEx;
    @XmlElement(name = "Citations")
    protected CitationsType citations;
    @XmlElement(name = "Modifiers")
    protected ModifiersType modifiers;
    @XmlElement(name = "Properties")
    protected PropertiesType properties;
    @XmlElement(name = "CreateDate")
    protected String createDate;
    @XmlElement(name = "UpdateDate")
    protected String updateDate;
    @XmlElement(name = "PubDate")
    protected String pubDate;
    @XmlElement(name = "AkaTaxIds")
    protected AkaTaxIdsType akaTaxIds;

    /**
     * Gets the value of the taxId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxId() {
        return taxId;
    }

    /**
     * Sets the value of the taxId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxId(String value) {
        this.taxId = value;
    }

    /**
     * Gets the value of the scientificName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScientificName() {
        return scientificName;
    }

    /**
     * Sets the value of the scientificName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScientificName(String value) {
        this.scientificName = value;
    }

    /**
     * Gets the value of the otherNames property.
     * 
     * @return
     *     possible object is
     *     {@link OtherNamesType }
     *     
     */
    public OtherNamesType getOtherNames() {
        return otherNames;
    }

    /**
     * Sets the value of the otherNames property.
     * 
     * @param value
     *     allowed object is
     *     {@link OtherNamesType }
     *     
     */
    public void setOtherNames(OtherNamesType value) {
        this.otherNames = value;
    }

    /**
     * Gets the value of the parentTaxId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentTaxId() {
        return parentTaxId;
    }

    /**
     * Sets the value of the parentTaxId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentTaxId(String value) {
        this.parentTaxId = value;
    }

    /**
     * Gets the value of the rank property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRank() {
        return rank;
    }

    /**
     * Sets the value of the rank property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRank(String value) {
        this.rank = value;
    }

    /**
     * Gets the value of the division property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDivision() {
        return division;
    }

    /**
     * Sets the value of the division property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDivision(String value) {
        this.division = value;
    }

    /**
     * Gets the value of the geneticCode property.
     * 
     * @return
     *     possible object is
     *     {@link GeneticCodeType }
     *     
     */
    public GeneticCodeType getGeneticCode() {
        return geneticCode;
    }

    /**
     * Sets the value of the geneticCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeneticCodeType }
     *     
     */
    public void setGeneticCode(GeneticCodeType value) {
        this.geneticCode = value;
    }

    /**
     * Gets the value of the mitoGeneticCode property.
     * 
     * @return
     *     possible object is
     *     {@link MitoGeneticCodeType }
     *     
     */
    public MitoGeneticCodeType getMitoGeneticCode() {
        return mitoGeneticCode;
    }

    /**
     * Sets the value of the mitoGeneticCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link MitoGeneticCodeType }
     *     
     */
    public void setMitoGeneticCode(MitoGeneticCodeType value) {
        this.mitoGeneticCode = value;
    }

    /**
     * Gets the value of the lineage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLineage() {
        return lineage;
    }

    /**
     * Sets the value of the lineage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLineage(String value) {
        this.lineage = value;
    }

    /**
     * Gets the value of the lineageEx property.
     * 
     * @return
     *     possible object is
     *     {@link LineageExType }
     *     
     */
    public LineageExType getLineageEx() {
        return lineageEx;
    }

    /**
     * Sets the value of the lineageEx property.
     * 
     * @param value
     *     allowed object is
     *     {@link LineageExType }
     *     
     */
    public void setLineageEx(LineageExType value) {
        this.lineageEx = value;
    }

    /**
     * Gets the value of the citations property.
     * 
     * @return
     *     possible object is
     *     {@link CitationsType }
     *     
     */
    public CitationsType getCitations() {
        return citations;
    }

    /**
     * Sets the value of the citations property.
     * 
     * @param value
     *     allowed object is
     *     {@link CitationsType }
     *     
     */
    public void setCitations(CitationsType value) {
        this.citations = value;
    }

    /**
     * Gets the value of the modifiers property.
     * 
     * @return
     *     possible object is
     *     {@link ModifiersType }
     *     
     */
    public ModifiersType getModifiers() {
        return modifiers;
    }

    /**
     * Sets the value of the modifiers property.
     * 
     * @param value
     *     allowed object is
     *     {@link ModifiersType }
     *     
     */
    public void setModifiers(ModifiersType value) {
        this.modifiers = value;
    }

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link PropertiesType }
     *     
     */
    public PropertiesType getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link PropertiesType }
     *     
     */
    public void setProperties(PropertiesType value) {
        this.properties = value;
    }

    /**
     * Gets the value of the createDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreateDate() {
        return createDate;
    }

    /**
     * Sets the value of the createDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreateDate(String value) {
        this.createDate = value;
    }

    /**
     * Gets the value of the updateDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdateDate() {
        return updateDate;
    }

    /**
     * Sets the value of the updateDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdateDate(String value) {
        this.updateDate = value;
    }

    /**
     * Gets the value of the pubDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPubDate() {
        return pubDate;
    }

    /**
     * Sets the value of the pubDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPubDate(String value) {
        this.pubDate = value;
    }

    /**
     * Gets the value of the akaTaxIds property.
     * 
     * @return
     *     possible object is
     *     {@link AkaTaxIdsType }
     *     
     */
    public AkaTaxIdsType getAkaTaxIds() {
        return akaTaxIds;
    }

    /**
     * Sets the value of the akaTaxIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link AkaTaxIdsType }
     *     
     */
    public void setAkaTaxIds(AkaTaxIdsType value) {
        this.akaTaxIds = value;
    }

}
