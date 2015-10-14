Pull Back Layout
==========

[![](https://img.shields.io/github/tag/oxoooo/pull-back-layout.svg?style=flat-square)](https://jitpack.io/#oxoooo/pull-back-layout)
[![](https://img.shields.io/github/license/oxoooo/pull-back-layout.svg?style=flat-square)](LICENSE)
[![](https://img.shields.io/github/watchers/oxoooo/pull-back-layout.svg?style=flat-square)](https://github.com/oxoooo/pull-back-layout/watchers)
[![](https://img.shields.io/github/stars/oxoooo/pull-back-layout.svg?style=flat-square)](https://github.com/oxoooo/pull-back-layout/stargazers)
[![](https://img.shields.io/github/forks/oxoooo/pull-back-layout.svg?style=flat-square)](https://github.com/oxoooo/pull-back-layoutt/network)
[![](https://img.shields.io/github/issues/oxoooo/pull-back-layout.svg?style=flat-square)](https://github.com/oxoooo/pull-back-layout/issues)

Pull down to finish an Activity.

![](screenshot.gif)

## Download

```gradle
repositories {
    // ...
    maven { url "https://jitpack.io" }
}

dependencies {
    // ... support library ...
    // ...
    compile 'com.github.oxoooo:pull-back-layout:1.0.0'
}
```

## Usage

You may refer to one of our apps that already using this library: [mr-mantou-android](https://github.com/oxoooo/mr-mantou-android/blob/abc2b660b3c0e6ed5c6fe5fb962e7df19ab9d8a4/app/src/main/java/ooo/oxo/mr/ViewerActivity.java#L170)

1. Wraps `ooo.oxo.library.widget.PullBackLayout` around your `ImageView` or `ViewPager`:

  ```xml
  <ooo.oxo.library.widget.PullBackLayout
      android:id="@+id/puller"
      android:layout_width="match_parent"
      android:layout_height="match_parent">

      <android.support.v4.view.ViewPager
          android:id="@+id/pager"
          android:layout_width="match_parent"
          android:layout_height="match_parent" />

  </ooo.oxo.library.widget.PullBackLayout>
  ```

2. Set a callback:

  ```java
  public class ViewerActivity extends AppCompatActivity implements PullBackLayout.Callback {

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          /* ... */
          puller.setCallback(this);
      }

      @Override
      public void onPullStart() {
          // fade out Action Bar ...
          // show Status Bar ...
      }

      @Override
      public void onPull(float progress) {
          // set the opacity of the window's background
      }

      @Override
      public void onPullCancel() {
          // fade in Action Bar
      }

      @Override
      public void onPullComplete() {
          supportFinishAfterTransition();
      }

  }
  ```

## License

[MIT License](LICENSE)
