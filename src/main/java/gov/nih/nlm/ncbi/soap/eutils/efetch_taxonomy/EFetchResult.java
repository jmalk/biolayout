
package gov.nih.nlm.ncbi.soap.eutils.efetch_taxonomy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ERROR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element ref="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}TaxaSet" minOccurs="0"/>
 *         &lt;element name="IdList" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}IdListType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "error",
    "taxaSet",
    "idList"
})
@XmlRootElement(name = "eFetchResult")
public class EFetchResult {

    @XmlElement(name = "ERROR")
    protected String error;
    @XmlElement(name = "TaxaSet")
    protected TaxaSet taxaSet;
    @XmlElement(name = "IdList")
    protected IdListType idList;

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getERROR() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setERROR(String value) {
        this.error = value;
    }

    /**
     * Gets the value of the taxaSet property.
     * 
     * @return
     *     possible object is
     *     {@link TaxaSet }
     *     
     */
    public TaxaSet getTaxaSet() {
        return taxaSet;
    }

    /**
     * Sets the value of the taxaSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaxaSet }
     *     
     */
    public void setTaxaSet(TaxaSet value) {
        this.taxaSet = value;
    }

    /**
     * Gets the value of the idList property.
     * 
     * @return
     *     possible object is
     *     {@link IdListType }
     *     
     */
    public IdListType getIdList() {
        return idList;
    }

    /**
     * Sets the value of the idList property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdListType }
     *     
     */
    public void setIdList(IdListType value) {
        this.idList = value;
    }

}
