
package gov.nih.nlm.ncbi.soap.eutils.efetch_taxonomy;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LineageExType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LineageExType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Taxon" type="{http://www.ncbi.nlm.nih.gov/soap/eutils/efetch_taxonomy}TaxonType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LineageExType", propOrder = {
    "taxon"
})
public class LineageExType {

    @XmlElement(name = "Taxon")
    protected List<TaxonType> taxon;

    /**
     * Gets the value of the taxon property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the taxon property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTaxon().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TaxonType }
     * 
     * 
     */
    public List<TaxonType> getTaxon() {
        if (taxon == null) {
            taxon = new ArrayList<TaxonType>();
        }
        return this.taxon;
    }

}
