
# StudyRouter
> 项目地址：https://github.com/Double2hao/StudyRouter
> 
此项目通过使用动态编译、注解解释器等技术，用最简单的方式实现了路由的自动注册。
如对这方面知识有兴趣的朋友可以看看。

<img src="https://img-blog.csdnimg.cn/20200419075517689.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0RvdWJsZTJoYW8=,size_16,color_FFFFFF,t_70" width = 20% height = 20% />

# 实现

 1. 通过注解解释器生成辅助类
 2. 通过Transform出辅助类和要注入代码的位置。
 3. 在相应的位置调用辅助类的相关代码，实现自动注册。

# 涉及知识点列表
笔者的整个学习过程都写了文章记录，下面是这些知识点对应的文章。

 - 注解入门：https://xujiajia.blog.csdn.net/article/details/104861727
 - 编译时注解：https://xujiajia.blog.csdn.net/article/details/105027291
 - 动态编译入门：https://xujiajia.blog.csdn.net/article/details/105173087
 - java操作数栈和局部变量表：https://xujiajia.blog.csdn.net/article/details/105322072
 - java ASM：https://xujiajia.blog.csdn.net/article/details/105322832
 - ARouter 初始化源码：https://xujiajia.blog.csdn.net/article/details/105323381

# 后记 2020.4.19
一开始学习这块知识也只是出于好奇，一直很想知道为什么ARouter这些框架可以直接写一个注解就可以直接调用。

学完注解后，发现需要懂注解解释器才能动态生成java，学完注解解释器发现要学习动态编译才能扫描文件或者注入代码。

整个过程看似挺流畅，但这些零散知识点学完了也还是觉得缺点什么，觉得自己并没有真正掌握这些知识。

于是就有了写StudyRouter这个项目的想法。

果然，真的实现的时候也没有想象中那么简单，碰到一些细节的问题，也是耗了挺多时间。
好在最终也是将这些知识点的整合做完了。

也是和开头说的一样，对这方面知识点感兴趣的读者可以看一看。
