package com.sun.corba.se.PortableActivationIDL;


/**
* com/sun/corba/se/PortableActivationIDL/ORBProxyHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from c:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8u91/7017/corba/src/share/classes/com/sun/corba/se/PortableActivationIDL/activation.idl
* Friday, May 20, 2016 5:44:10 PM PDT
*/


/** ORB callback interface, passed to Activator in registerORB method.
    */
abstract public class ORBProxyHelper
{
  private static String  _id = "IDL:PortableActivationIDL/ORBProxy:1.0";

  public static void insert (org.omg.CORBA.Any a, ORBProxy that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static ORBProxy extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (ORBProxyHelper.id (), "ORBProxy");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static ORBProxy read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_ORBProxyStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, ORBProxy value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static ORBProxy narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof ORBProxy)
      return (ORBProxy)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      _ORBProxyStub stub = new _ORBProxyStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static ORBProxy unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof ORBProxy)
      return (ORBProxy)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      _ORBProxyStub stub = new _ORBProxyStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
