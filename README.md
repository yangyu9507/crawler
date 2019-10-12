# crawler
Java从阿里1688网站定时爬取热搜词

请预先安装Chrome Driver

首先，需要在Linux服务器上安装Chrome和ChromeDriver 

1.	用下面的命令安装最新的 Google Chrome

yum install https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm

安装必要的库

yum install mesa-libOSMesa-devel gnu-free-sans-fonts wqy-zenhei-fonts

2、安装 chromedriver

chrome官网   wget https://chromedriver.storage.googleapis.com/78.0.3904.11/chromedriver_linux64.zip
淘宝源（推荐）wget https://npm.taobao.org/mirrors/chromedriver/78.0.3904.11/chromedriver_linux64.zip 

版本号按需要进行选择, 地址: https://npm.taobao.org/mirrors/chromedriver/
Windows上需要在上述地址下载 chromedriver_win32.zip,  解压后和 chrome.exe  放在同一文件夹下
