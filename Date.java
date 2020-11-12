/**
 * Date.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package a3;

import java.util.Calendar;
import java.util.TimeZone;

public class Date  implements java.io.Serializable {
    private short day;

    private short month;

    private short year;

    public Date() {
    }

    public Date(
           short day,
           short month,
           short year) {
           this.day = day;
           this.month = month;
           this.year = year;
    }


    /**
     * Gets the day value for this Date.
     * 
     * @return day
     */
    public short getDay() {
        return day;
    }


    /**
     * Sets the day value for this Date.
     * 
     * @param day
     */
    public void setDay(short day) {
        this.day = day;
    }


    /**
     * Gets the month value for this Date.
     * 
     * @return month
     */
    public short getMonth() {
        return month;
    }


    /**
     * Sets the month value for this Date.
     * 
     * @param month
     */
    public void setMonth(short month) {
        this.month = month;
    }


    /**
     * Gets the year value for this Date.
     * 
     * @return year
     */
    public short getYear() {
        return year;
    }


    /**
     * Sets the year value for this Date.
     * 
     * @param year
     */
    public void setYear(short year) {
        this.year = year;
    }
    
    public String toString() {
  	  return day + "-" + month + "-" + year;
    }
    
    public static Date getCurrentDate() {
  		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
  		java.util.Date date = calendar.getTime();
  		short day = (short)calendar.get(Calendar.DATE);
  		short month = (short)(calendar.get(Calendar.MONTH) + 1);
  		short year = (short)calendar.get(Calendar.YEAR);
  		return new Date(day, month, year);
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Date)) return false;
        Date other = (Date) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.day == other.getDay() &&
            this.month == other.getMonth() &&
            this.year == other.getYear();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getDay();
        _hashCode += getMonth();
        _hashCode += getYear();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Date.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://a3/", "date"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("day");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Day"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("month");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Month"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("year");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Year"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "short"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
