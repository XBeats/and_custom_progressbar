# and\_custom\_progressbar


## Features    

- 比较炫酷的自定义ProgressBar  
- 自定义进度，刷新频率，移动间距， 倾斜角度  


## Usage

#### 主要思想
主要有SurfaceView来实现的，毕竟需要一个线程去动态显示  
主要由3层构成，如下图：

![image](https://raw.githubusercontent.com/XBeats/and_custom_progressbar/master/screenshot/ceng.png)

第一层：背景白色  
第二层：浅色  
第三层：深色图形  

通过偏移量控制整体滚动效果


## ScreenShot（效果图）

![image](https://raw.githubusercontent.com/XBeats/and_custom_progressbar/master/screenshot/progressbar.gif) 