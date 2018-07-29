# UpdateVersion
用于版本更新的模块

1.需要在CheckUpdataUtil类中添加以下字段
       //版本更新的网络请求
       String url = "";//跟服务器确定的有关版本更新的网络请求
       //返回的结果，根据项目中请求框架来写。
       String result = "";
       //之后的返回的json字段根据实际情况解析
2.这个模块使用了
       //权限请求库
       implementation 'com.yanzhenjie:permission:2.0.0-rc11'
       //网络请求库
       impementation 'org.xutils:xutils:3.3.34'//这个网络请求库可以根据实际项目的请求库进行调整。
3.进行版本更新时
       CheckUpdataUtil checkUpdataUtil = new CheckUpdataUtil(PersonalCenterActivity.this);
                       checkUpdataUtil.IsUpdate();
       //更改这个类中的请求和解析就可以了