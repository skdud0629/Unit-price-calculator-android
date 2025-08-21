package com.mathinskills.calculator

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 시스템 인셋 적용 (edge-to-edge일 때 상태바/내비게이션바 겹침 방지)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // WebView 참조
        webView = findViewById(R.id.webView)

        // WebView 기본 설정
        webView.webViewClient = WebViewClient() // 외부 브라우저 말고 WebView 내부에서 열리게
        webView.settings.apply {
            javaScriptEnabled = true                      // 필요한 경우에만 활성화
            domStorageEnabled = true                      // 일부 사이트 동작에 필요
            cacheMode = WebSettings.LOAD_DEFAULT
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
        }

        // 페이지 로드
        if (savedInstanceState == null) {
            webView.loadUrl("https://www.skillsinmath.com/login")
        } else {
            // 프로세스 재생성 시 상태 복원
            webView.restoreState(savedInstanceState)
        }

        // 하드웨어 뒤로가기: WebView의 뒤로가기가 우선
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (this@MainActivity::webView.isInitialized && webView.canGoBack()) {
                    webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    // 화면 회전/프로세스 재생성 대비 WebView 상태 저장
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (this::webView.isInitialized) {
            webView.saveState(outState)
        }
    }

    // 메모리 누수 방지
    override fun onDestroy() {
        if (this::webView.isInitialized) {
            webView.destroy()
        }
        super.onDestroy()
    }
}
