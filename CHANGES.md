[1.0.0-SNAPSHOT]

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