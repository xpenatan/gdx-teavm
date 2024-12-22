[-SNAPSHOT]

[1.0.5]
- Add Config Asset preloadListener
- Update AssetLoader and AssetDownloader
- Add AssetInstance to obtain AssetLoader or AssetDownloader
- Fix drawing to Gdx2DPixmapEmu
- Update Freetype emulation to fix build errors and script loading solution
- Update TeaVM to 0.11.0

[1.0.4]
- Fix music id
- add custom gltf resource path
- Code improvement when using physical pixels
- Fix file handle list returning wrong path
- UsePhysicalPixels is set to false by default
- Fix webgl buffers

[1.0.3]
- Don't download assets if preload assets is true
- add PowerPreference option
- update teavm to 0.10.1
- update upload attempt
- Small file handle fix
- Add option to overwrite saved file
- Improve download logs and progress method

[1.0.2]
- Small file storage improvement
- Use file type for checking if asset is loaded
- Improve arraybuffer performance
- Improve texture opengl method
- Fix sync loading (FreeType)

[1.0.1]
- Fix an issue when creating database and trying to load assets.
- Quick fix to filter out assets.txt file and to delete it before adding new files

[1.0.0]
- Update teavm to 0.10.0
- Pixmap now use Gdx2DPixmap
- New Gdx.files.internal, classpath and local implementation. Local files uses IndexedDB.  
- Call dispose when browser closes
- Improve clipboard text copy/paste.
- Change default sound/music api to howler.js
- add shouldEncodePreference config
- add localStoragePrefix config
- AssetManager can now download assets

[1.0.0-b9]
- add TeaClassFilter printAllowedClasses() and printExcludedClasses()
- add TeaReflectionSupplier.printReflectionClasses()
- Change exclude class comparison to regex
- Downgrade teavm dev 0.10.X version to 0.9.2 release version
- Remove tool/generator from maven/snapshot.

[1.0.0-b8]
- Fix MMB code
- Fix catch key
- Support IntBuffer in GL20
- Update teavm to 0.10.0-dev-1
- Update libgdx to 1.12.1
- Remove obfuscate from build config. Use TeaVMTool.
- Add useDefaultHtmlIndex and logoPath config option.

[1.0.0-b7]
- Remove jParser
- Remove/Move Bullet module to a standalone repository
- Remove/Move Box2d module to a standalone repository
- Remove hardcoded box2d, bullet and imgui load method.
- Update teavm to version 0.9.0-dev-12
- Update GL20 from GWT gdx 1.12.0
- Update Sound from GWT gdx 1.12.0
- Add GL30 from GWT gdx 1.12.0
- Add resources automatically when module contains META-INF/gdx-teavm.properties
- Fix freetype shadow error
- Improve rendering performance
- Add mouse locking

[1.0.0-b6]
- Update libgdx to version 1.12.0
- Update jParser to version b3
- Update teavm to version 0.9.0-dev-7

[1.0.0-b5]
- Option in App config to show download logs. Default is set to false.
- Implement setApplicationLogger method.
- Rename bullet desktop lib from platform to desktop.
- Add file drag & drop using TeaWindowListener in App config.
- Add WIP Gdx2DPixmap. Can toggle in App config.
- Update teavm to 0.9.0-dev-4
- Add userAgent system property
- Add Storage prefix #94

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