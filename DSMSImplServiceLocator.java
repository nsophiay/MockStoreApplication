/**
 * DSMSImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package a3;

public class DSMSImplServiceLocator extends org.apache.axis.client.Service implements a3.DSMSImplService {

    public DSMSImplServiceLocator() {
    }


    public DSMSImplServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public DSMSImplServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for DSMSImplPort
    private java.lang.String DSMSImplPort_address = "http://localhost:8080/a3/WebInterface";

    public java.lang.String getDSMSImplPortAddress() {
        return DSMSImplPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String DSMSImplPortWSDDServiceName = "DSMSImplPort";

    public java.lang.String getDSMSImplPortWSDDServiceName() {
        return DSMSImplPortWSDDServiceName;
    }

    public void setDSMSImplPortWSDDServiceName(java.lang.String name) {
        DSMSImplPortWSDDServiceName = name;
    }

    public a3.WebInterface getDSMSImplPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(DSMSImplPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getDSMSImplPort(endpoint);
    }

    public a3.WebInterface getDSMSImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            a3.DSMSImplPortBindingStub _stub = new a3.DSMSImplPortBindingStub(portAddress, this);
            _stub.setPortName(getDSMSImplPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setDSMSImplPortEndpointAddress(java.lang.String address) {
        DSMSImplPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (a3.WebInterface.class.isAssignableFrom(serviceEndpointInterface)) {
                a3.DSMSImplPortBindingStub _stub = new a3.DSMSImplPortBindingStub(new java.net.URL(DSMSImplPort_address), this);
                _stub.setPortName(getDSMSImplPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("DSMSImplPort".equals(inputPortName)) {
            return getDSMSImplPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://a3/", "DSMSImplService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://a3/", "DSMSImplPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("DSMSImplPort".equals(portName)) {
            setDSMSImplPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
