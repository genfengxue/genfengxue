# genfengxue
Android client for gale


## 新版本发布流程
* AndroidManifest.xml中修改versionCode和versionName
* git tag versionName && git push && git push --tags
* 打包到处apk文件
* 将文件上传至七牛存储，使用前缀apk/
* 服务器端插入新的更新记录 coffee scripts/insert_release_note.coffee
