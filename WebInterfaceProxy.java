package a3;

public class WebInterfaceProxy implements a3.WebInterface {
  private String _endpoint = null;
  private a3.WebInterface webInterface = null;
  
  public WebInterfaceProxy() {
    _initWebInterfaceProxy();
  }
  
  public WebInterfaceProxy(String endpoint) {
    _endpoint = endpoint;
    _initWebInterfaceProxy();
  }
  
  private void _initWebInterfaceProxy() {
    try {
      webInterface = (new a3.DSMSImplServiceLocator()).getDSMSImplPort();
      if (webInterface != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)webInterface)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)webInterface)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (webInterface != null)
      ((javax.xml.rpc.Stub)webInterface)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public a3.WebInterface getWebInterface() {
    if (webInterface == null)
      _initWebInterfaceProxy();
    return webInterface;
  }
  
  public boolean addItem(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2, short arg3, double arg4) throws java.rmi.RemoteException{
    if (webInterface == null)
      _initWebInterfaceProxy();
    return webInterface.addItem(arg0, arg1, arg2, arg3, arg4);
  }
  
  public boolean removeItem(java.lang.String arg0, java.lang.String arg1, short arg2) throws java.rmi.RemoteException{
    if (webInterface == null)
      _initWebInterfaceProxy();
    return webInterface.removeItem(arg0, arg1, arg2);
  }
  
  public double purchaseItem(java.lang.String arg0, java.lang.String arg1, a3.Date arg2) throws java.rmi.RemoteException{
    if (webInterface == null)
      _initWebInterfaceProxy();
    return webInterface.purchaseItem(arg0, arg1, arg2);
  }
  
  public java.lang.String listItemAvailability(java.lang.String arg0) throws java.rmi.RemoteException{
    if (webInterface == null)
      _initWebInterfaceProxy();
    return webInterface.listItemAvailability(arg0);
  }
  
  public java.lang.String findItem(java.lang.String arg0, java.lang.String arg1) throws java.rmi.RemoteException{
    if (webInterface == null)
      _initWebInterfaceProxy();
    return webInterface.findItem(arg0, arg1);
  }
  
  public double returnItem(java.lang.String arg0, java.lang.String arg1, a3.Date arg2) throws java.rmi.RemoteException{
    if (webInterface == null)
      _initWebInterfaceProxy();
    return webInterface.returnItem(arg0, arg1, arg2);
  }
  
  public boolean exchangeItem(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) throws java.rmi.RemoteException{
    if (webInterface == null)
      _initWebInterfaceProxy();
    return webInterface.exchangeItem(arg0, arg1, arg2);
  }
  
  
}