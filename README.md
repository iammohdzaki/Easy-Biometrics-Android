# Easy-Biometrics-Android

[![](https://jitpack.io/v/iammohdzaki/Easy-Biometrics-Android.svg)](https://jitpack.io/#iammohdzaki/Easy-Biometrics-Android)

This is Android/Kotlin library which allows you to do fingerprint auth in android.
- Custom Callback 
- Default UI

For Gradle : 
1. Add the JitPack repository to your build file
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
2. Add the dependency
```
dependencies {
	      implementation 'com.github.iammohdzaki:Easy-Biometrics-Android:$latestVersion'

	}
```

How To Use

For Default UI :
```
BiometricsDialogHelper.Builder(this)
                .title(getString(R.string.knotty_verification))
                .description(getString(R.string.verification_description))
                .negativeText(getString(R.string.cancel_text))
                .callback(object : Callback {
                    override fun success() {
                        openActivity(DashboardActivity::class.java, true)
                    }

                    override fun failure(message: String) {
                        showSnackBar(message, StatusCodes.FAILED)
                    }

                })
                .create().show()
```

For Custom UI: 
```
// Register Fingerprint Auth
fun registerAuthEvent()

//Remove Fingerprint Auth
fun removeAuthEvent()

```


License
```
The MIT License (MIT)

Copyright (c) 2021 Mohammad Zaki

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
