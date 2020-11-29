## README

该程序是用来生成软件测试中的covering array，目前只支持2-way和3-way的情况



界面如图所示

![image-20201129120737023](C:\Users\91766\AppData\Roaming\Typora\typora-user-images\image-20201129120737023.png)

本程序只支持对excel文件.xls的读写，因此将写好的样例保存到excel文件中，再依次选择2-way或者3-way然后再选择文件即可。

输入的excel格式应为：

- 第一行保存各变量名称
- 每个变量名的取值放在该变量名对应的列的下方

（详情可见附件中的输入样例）

生成的时候会在控制台输出已经生成的数目，当运行结束后会弹出消息框

![image-20201129121317520](C:\Users\91766\AppData\Roaming\Typora\typora-user-images\image-20201129121317520.png)

最后生成好的结果会保存到当前文件夹下的2-way.xls或者3-way.xls文件中

