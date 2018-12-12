# TakeImage

[![jitpack](https://jitpack.io/v/xiaohaibin/TakeImageDialog.svg)](https://jitpack.io/#xiaohaibin/TakeImageDialog)

简洁的图片选择对话框

- 适配7.0以上设备

### 效果图

![](https://github.com/xiaohaibin/TakeImage/blob/master/screenshot/device-01.png)

### 使用

Step 1. Add it in your root build.gradle at the end of repositories:
```
allprojects {
     repositories {
	...
	maven { url 'https://jitpack.io' }
     }
}

```
Step 2. Add the dependency
```
dependencies {
    //将latestVersion替换成上面 jitpack 后面的版本号
    iimplementation 'com.github.xiaohaibin:TakeImageDialog:latestVersion'
}
```
Step 3. 代码中使用

```

  ChooseImageDialog chooseImageDialog = ChooseImageDialog.newInstance();
        chooseImageDialog.setOperator(new ChooseImageDialog.Operator() {
            @Override
            public void onGetImage(String imaePath) {
                Log.i("===>path",imaePath);
                ((ImageView) findViewById(R.id.iv)).setImageURI(Uri.fromFile(new File(imaePath)));
                Toast.makeText(MainActivity.this, imaePath, Toast.LENGTH_SHORT).show();
            }
        });
        chooseImageDialog.show(getSupportFragmentManager(), null);
	
```

### 关于我

* **Email**: <xhb_199409@163.com>
* **Home**: <http://www.jxnk25.club>
* **掘金**: <https://juejin.im/user/56fcba0a71cfe4005ca1a57b>
* **简书**: <http://www.jianshu.com/users/42aed90cf5af/latest_articles>

### 欢迎关注个人微信公众号，将会定期推送优质技术文章，求关注

![欢迎关注“大话微信”公众号](http://upload-images.jianshu.io/upload_images/1956769-2f49dcb0dc5195b6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

License
--
    Copyright (C) 2016 xhb_199409@163.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    

