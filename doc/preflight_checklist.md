ChangeLog
-----------------------
* Update NEWS.md
* Update README.md
* Commit and push

FFmpeg
-----------------------
* Build and test iOS and Android demo locally
* Modify ffmpeg version in init-ios.sh and init-android.sh
* Modify ffmpeg version in `IJKFFMoviePlayerController` (by running `sh init-ios.sh`)
* Commit and push

OpenSSL
----------------------
* Check openssl latest stable version

ijkplayer
-----------------------
* Update version.sh
* Create a tag (subtitle)
* Commit and push (TAG ONLY)

Travis-ci
-----------------------
* Modify ijk version in `.travis.yaml` in iOS and Android ci repo.
* Ensure compile task has been started on travis-ci.
* Ensure Andoird release has been released in bintray.

Take off
-----------------------
* Push master to github

Track 表示一些sample的集合，对于媒体数据说，track表示一个视频或者音频序列

Hint track 这个特殊的track并不包含媒体数据，而是包含一些将其他数据track打包成流媒体的指示信息
sample 

Mp4文件的生成与解析，播放
两个重要的box，moov and mdat

Mp4 文件格式经常使用几个不同的概念，理解其不同是理解这个文件格式的关键

结构
1，ftype
2，moov
2.1mvhd

2.2track
2.2.1 tkhd

2.2.2 mdia