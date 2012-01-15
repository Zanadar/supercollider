OSXPlatform : UnixPlatform
{
	var <>preferencesAction;
	var <>sleepAction, <>wakeAction, <>isSleeping=false;

	initPlatform {
		super.initPlatform;
		recordingsDir = "~/Music/SuperCollider Recordings".standardizePath;
		this.declareFeature(\unixPipes); // pipes are possible (can't declare in UnixPlatform since IPhonePlatform is unixy yet can't support pipes)
		if (Platform.ideName == "scapp") { this.setDeferredTaskInterval(1/60); }
	}

	name { ^\osx }

	startupFiles {
		var filename = "startup.rtf";
		var deprecated = [this.systemAppSupportDir +/+ filename, this.userAppSupportDir +/+ filename];
		Platform.deprecatedStartupFiles(deprecated);
		^(deprecated ++ super.startupFiles)
	}

	startup {
		this.loadStartupFiles;
		if(Platform.ideName == "scapp"){
			Document.implementationClass.startup;
			// make server window
			Server.internal.makeWindow;
			Server.local.makeWindow;
		};
	}
	shutdown {
		HIDDeviceService.releaseDeviceList;
		if(Platform.ideName == "scapp"){
			CocoaMenuItem.clearCustomItems;
		};
	}

	defaultGUIScheme { if (GUI.get(\cocoa).isNil) {^\qt} {^\cocoa} }
	defaultHIDScheme { ^\osx_hid }

	setDeferredTaskInterval { |interval| _SetDeferredTaskInterval }

	findHelpFile { | string |
		^string.findHelpFile;
	}

	getMouseCoords {
		^this.prGetMouseCoords(Point.new);
	}

	prGetMouseCoords {|point|
		_Mouse_getCoords
		^this.primitiveFailed
	}

	// for now just write syntax colours. Could be other things.
	writeClientCSS {
		var theme, file, string;
		theme = Document.theme;
		SCDoc.helpTargetDir.mkdir;
		file = File.open(SCDoc.helpTargetDir ++ "/frontend.css", "w");
		string = ".str { color: %; } /* strings */
.kwd { color: %; } /* special values like true, nil.. */
.com { color: %; } /* comments */
.typ { color: %; } /* class names */
.lit { color: %; } /* numbers and character literals */
.pun { color: %; } /* punctuation */
.pln { color: %; } /* plain text, methods and variable names */
.tag { color: %; } /* special variables like super, thisProcess... */
.dec { color: %; } /* declarations like var, const... */
.atn { color: %; } /* symbols */
.atv { color: %; } /* environment vars */".format(
			theme.stringColor.hexString,
			theme.specialValsColor.hexString,
			theme.commentColor.hexString,
			theme.classColor.hexString,
			theme.numberColor.hexString,
			theme.puncColor.hexString,
			theme.textColor.hexString,
			theme.specialVarsColor.hexString,
			theme.declColor.hexString,
			theme.symbolColor.hexString,
			theme.environColor.hexString
		);
		file.putString(string);
		file.close;
	}
}
