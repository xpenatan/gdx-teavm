[1.0.0-SNAPSHOT]
- Option in App config to show download logs. Default is set to false.
- Implement setApplicationLogger method.
- Rename bullet desktop lib from platform to desktop

[1.0.0-b4]
- Fix an issue with TeaClassloader that was giving annotation error when building artemis games
- Bugfix: Asset Loading should not re-Download 403 Errors
- Bugfix for density calculation.
- Bugfix: blank line on top of HTML body on mobile.
- Bugfix: Asset Loading & Error "ERR_HTTP2_SERVER_REFUSED_STREAM

[1.0.0-b3]
- Add gdx-bullet c++ module so desktop and web use the same c++ code/version
- Update teaVM to 0.8.0-dev-1 and project Java version to 11
- Add missing buffer in glBufferData
- Add web gl debug
- Bugfix for File Listing: file listing now also includes binary, image and audio files
- Build Configuration: Asset Filter & Title for HTML File can now be Configured
- Load imgui javascript file if exist (Temp)
- Add Bullet missing methods and improvements
- Remove backend-web
- Deprecate some settings from TeaBuildConfiguration (obfuscate flag)
- Add webapp loading
- Add TeaNet HTTP Requests & Opening Links
- Fixing the base URL for asset loading. Having a '?' with parameters won't work.
- Option to not show the libGDX logo during loading

[1.0.0-b2]
- Fix sound/music assets path
- Add TeaBuildConfiguration.classesToSkip option
- Remove MathUtilsEmu

[1.0.0-b1]
- First beta release