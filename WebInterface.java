/**
 * WebInterface.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package a3;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService()
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface WebInterface extends java.rmi.Remote {
	@WebMethod()
    public boolean addItem(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2, short arg3, double arg4) throws java.rmi.RemoteException;
	@WebMethod()
	public boolean removeItem(java.lang.String arg0, java.lang.String arg1, short arg2) throws java.rmi.RemoteException;
	@WebMethod()
	public double purchaseItem(java.lang.String arg0, java.lang.String arg1, a3.Date arg2) throws java.rmi.RemoteException;
	@WebMethod()
	public java.lang.String listItemAvailability(java.lang.String arg0) throws java.rmi.RemoteException;
	@WebMethod()
	public java.lang.String findItem(java.lang.String arg0, java.lang.String arg1) throws java.rmi.RemoteException;
	@WebMethod()
	public double returnItem(java.lang.String arg0, java.lang.String arg1, a3.Date arg2) throws java.rmi.RemoteException;
	@WebMethod()
	public boolean exchangeItem(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) throws java.rmi.RemoteException;
}
