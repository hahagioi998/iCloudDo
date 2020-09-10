package com.sun.corba.se.PortableActivationIDL;

/**
* com/sun/corba/se/PortableActivationIDL/ServerProxyHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from c:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8u91/7017/corba/src/share/classes/com/sun/corba/se/PortableActivationIDL/activation.idl
* Friday, May 20, 2016 5:44:10 PM PDT
*/


/** Server callback interface, passed to Activator in registerServer method.
    */
public final class ServerProxyHolder implements org.omg.CORBA.portable.Streamable
{
  public ServerProxy value = null;

  public ServerProxyHolder ()
  {
  }

  public ServerProxyHolder (ServerProxy initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = ServerProxyHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    ServerProxyHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return ServerProxyHelper.type ();
  }

}
