package com.mycompany.app;

import org.beetl.sql.core.mapper.BaseMapper;
import org.osgl.mvc.annotation.GetAction;

import act.Act;
import act.boot.app.RunApp;
import act.conf.AppConfigKey;
import act.util.SubClassFinder;

/**
 * Hello world!
 */
public class MyApp {

    @GetAction
    public String sayHello() {
        return "Hello World!";
    }
    
    @SubClassFinder(value = BaseMapper.class)
    public void initMapper(Class<? extends BaseMapper> cls){
    	System.out.println(cls);
    }

    public static void main(String[] args) throws Exception {
//    	   App.instance().scannerManager().register(new AppByteCodeScannerBase(){
//
//   			public ByteCodeVisitor byteCodeVisitor() {
//   				// TODO Auto-generated method stub
//   				return null;
//   			}
//
//   			public void scanFinished(String className) {
//   				// TODO Auto-generated method stub
//   				
//   			}
//
//   			@Override
//   			protected boolean shouldScan(String className) {
//   				System.out.println(className);
//   				return false ;
//   			}
//
//           	
//           });
    	
        RunApp.start(MyApp.class);
//        Act.appConfig().get(AppConfigKey.SCAN_PACKAGE);
     
        
    }
}