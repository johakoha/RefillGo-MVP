package com.example.ui.components

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.BuildConfig
import com.example.viewmodel.CampusStation
import org.json.JSONArray
import org.json.JSONObject

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapComponent(
    stations: List<CampusStation>,
    selectedPinId: String?,
    heatmapEnabled: Boolean,
    onPinSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Safety fallback for token storage to ensure 100% compilation
    val mapboxToken = remember {
        try {
            BuildConfig.MAPBOX_ACCESS_TOKEN
        } catch (e: Throwable) {
            ""
        }
    }

    // Convert stations metadata list to JSON array for JS injection
    val stationsJson = remember(stations) {
        val array = JSONArray()
        stations.forEach { st ->
            val obj = JSONObject()
            obj.put("id", st.id)
            obj.put("name", st.name)
            obj.put("lat", st.latitude)
            obj.put("lng", st.longitude)
            obj.put("status", st.status)
            obj.put("refills", st.refillsToday)
            obj.put("volume", st.availableDetergent)
            obj.put("saved", st.plasticSavedCount)
            
            val intensity = when (st.currentActivityLevel) {
                "High" -> "high"
                "Medium" -> "medium"
                else -> "low"
            }
            obj.put("intensity", intensity)
            array.put(obj)
        }
        array.toString()
    }

    // Precompiled Mapbox WebGL canvas wrapper in HTML
    val htmlContent = remember(stationsJson, mapboxToken) {
        """
        <!DOCTYPE html>
        <html>
        <head>
        <meta charset="utf-8">
        <title>RefillGo Mapbox</title>
        <meta name="viewport" content="initial-scale=1,maximum-scale=1,user-scalable=no">
        <link href="https://api.mapbox.com/mapbox-gl-js/v3.4.0/mapbox-gl.css" rel="stylesheet">
        <script src="https://api.mapbox.com/mapbox-gl-js/v3.4.0/mapbox-gl.js"></script>
        <style>
        body { margin: 0; padding: 0; background-color: #0F172A; }
        #map { position: absolute; top: 0; bottom: 0; width: 100%; height: 100%; }
        .custom-marker {
          display: flex !important;
          align-items: center;
          justify-content: center;
          width: 30px;
          height: 30px;
          background: #10B981;
          border: 2px solid white;
          border-radius: 50%;
          box-shadow: 0 0 10px rgba(16, 185, 129, 0.6);
          cursor: pointer;
          transition: all 0.25s ease-in-out;
        }
        .custom-marker:hover, .custom-marker.active {
          transform: scale(1.22) rotate(360deg);
          background: #0EA5E9 !important; /* Ocean Blue when active */
          box-shadow: 0 0 16px rgba(14, 165, 233, 0.95);
        }
        .pulse-glow {
          position: absolute;
          width: 44px;
          height: 44px;
          border: 2.5dp dashed #10B981;
          border-radius: 50%;
          animation: pulse 1.8s infinite ease-out;
          opacity: 0;
          pointer-events: none;
        }
        @keyframes pulse {
          0% { transform: scale(0.5); opacity: 0.85; }
          100% { transform: scale(1.6); opacity: 0; }
        }
        .mapboxgl-ctrl-bottom-left, .mapboxgl-ctrl-bottom-right {
          display: none !important;
        }
        </style>
        </head>
        <body>
        <div id="map"></div>
        <script>
        mapboxgl.accessToken = '$mapboxToken';

        const map = new mapboxgl.Map({
            container: 'map',
            style: 'mapbox://styles/mapbox/dark-v11', // Beautiful premium dark matching style
            center: [126.6535, 37.4502], // Inha University center
            zoom: 16.5,
            pitch: 55, // Pitch perspective
            bearing: -10,
            antialias: true
        });

        const stations = $stationsJson;
        const markersMap = {};

        map.on('style.load', () => {
            const layers = map.getStyle().layers;
            const labelLayerId = layers.find(layer => layer.type === 'symbol' && layer.layout['text-field'])?.id;

            // Enable volumetric 3D Buildings
            map.addLayer({
                'id': '3d-buildings',
                'source': 'composite',
                'source-layer': 'building',
                'filter': ['==', 'extrude', 'true'],
                'type': 'fill-extrusion',
                'minzoom': 15,
                'paint': {
                    'fill-extrusion-color': '#1E293B',
                    'fill-extrusion-height': [
                        'interpolate', ['linear'], ['zoom'],
                        15, 0,
                        15.05, ['get', 'height']
                    ],
                    'fill-extrusion-base': [
                        'interpolate', ['linear'], ['zoom'],
                        15, 0,
                        15.05, ['get', 'min_height']
                    ],
                    'fill-extrusion-opacity': 0.82
                }
            }, labelLayerId);

            // Add dynamic analytics Heatmap Activity Source
            const features = stations.map(st => ({
                "type": "Feature",
                "geometry": { "type": "Point", "coordinates": [st.lng, st.lat] },
                "properties": { "intensity": st.intensity === 'high' ? 4 : (st.intensity === 'medium' ? 2.5 : 1) }
            }));

            map.addSource('refill-demand', {
                'type': 'geojson',
                'data': {
                    "type": "FeatureCollection",
                    "features": features
                }
            });

            // Mapbox Heatmap spectrum centering activity density around dormitory pins
            map.addLayer({
                'id': 'demand-heat',
                'type': 'heatmap',
                'source': 'refill-demand',
                'layout': {
                    'visibility': '${if (heatmapEnabled) "visible" else "none"}'
                },
                'paint': {
                    'heatmap-weight': ['interpolate', ['linear'], ['get', 'intensity'], 0, 0, 4, 1],
                    'heatmap-intensity': ['interpolate', ['linear'], ['zoom'], 14, 1, 20, 3],
                    'heatmap-color': [
                        'interpolate', ['linear'], ['heatmap-value'],
                        0, 'rgba(16, 185, 129, 0)',
                        0.25, 'rgba(16, 185, 129, 0.4)',  // Green: Low
                        0.55, 'rgba(234, 179, 8, 0.7)',   // Yellow: Medium
                        0.8, 'rgba(249, 115, 22, 0.85)', // Orange: High
                        1.0, 'rgba(239, 68, 68, 0.95)'   // Red: Very High
                    ],
                    'heatmap-radius': ['interpolate', ['linear'], ['zoom'], 14, 40, 20, 150],
                    'heatmap-opacity': 0.82
                }
            }, labelLayerId);

            // Populate dormitory markers
            stations.forEach(st => {
                const container = document.createElement('div');
                container.style.position = 'relative';
                container.style.display = 'flex';
                container.style.justifyContent = 'center';
                container.style.alignItems = 'center';

                // Add active pulsation glows
                const glow = document.createElement('div');
                glow.className = 'pulse-glow';
                
                // Color meaning setup
                if (st.intensity === 'high') {
                    glow.style.borderColor = '#EF4444'; // Red hot aura
                } else if (st.intensity === 'medium') {
                    glow.style.borderColor = '#EAB308'; // Yellow medium aura
                } else {
                    glow.style.borderColor = '#10B981'; // Green low aura
                }
                container.appendChild(glow);

                // Main Pin
                const pin = document.createElement('div');
                pin.className = 'custom-marker';
                
                // Colors based on Status (Pilot SkyBlue vs Active EmeraldGreen)
                if (st.status === 'Pilot Location') {
                    pin.style.backgroundColor = '#0EA5E9'; // SkyBlue Pilot Accent
                    pin.innerHTML = '<span style="color:white;font-size:11px;font-weight:bold;">🚀</span>';
                } else {
                    pin.style.backgroundColor = '#10B981'; // Emerald Green standard
                    pin.innerHTML = '<span style="color:white;font-size:11px;font-weight:bold;">🌱</span>';
                }

                pin.onclick = () => {
                    selectMarker(st.id);
                    if (window.AndroidBridge) {
                        window.AndroidBridge.onMarkerClick(st.id);
                    }
                };
                container.appendChild(pin);

                const m = new mapboxgl.Marker(container)
                    .setLngLat([st.lng, st.lat])
                    .addTo(map);

                markersMap[st.id] = { marker: m, element: pin, glow: glow };
            });

            // Handle start selected highlight
            const initSel = '$selectedPinId';
            if (initSel && initSel !== 'null') {
                selectMarker(initSel);
            }
        });

        function selectMarker(id) {
            Object.keys(markersMap).forEach(key => {
                markersMap[key].element.classList.remove('active');
                markersMap[key].glow.style.display = 'block';
            });

            const target = markersMap[id];
            if (target) {
                target.element.classList.add('active');
                target.glow.style.display = 'none'; // focus view hides pulsation
                const st = stations.find(s => s.id === id);
                if (st) {
                    map.easeTo({
                        center: [st.lng, st.lat],
                        zoom: 17.5,
                        pitch: 60,
                        duration: 1200
                    });
                }
            }
        }

        function toggleHeatmap(visible) {
            if (map.getLayer('demand-heat')) {
                map.setLayoutProperty('demand-heat', 'visibility', visible ? 'visible' : 'none');
            }
        }

        function panToStation(lat, lng) {
            map.easeTo({
                center: [lng, lat],
                zoom: 17.5,
                pitch: 60,
                duration: 1200
            });
        }
        </script>
        </body>
        </html>
        """.trimIndent()
    }

    // Keep reference to the instantiated WebView to run dynamic JS calls safely
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    // Call JavaScript when outer compose state changes to avoid expensive re-rendering
    LaunchedEffect(heatmapEnabled, webViewRef) {
        webViewRef?.evaluateJavascript("if (typeof toggleHeatmap === 'function') { toggleHeatmap($heatmapEnabled); }", null)
    }

    LaunchedEffect(selectedPinId, webViewRef) {
        if (selectedPinId != null) {
            webViewRef?.evaluateJavascript("if (typeof selectMarker === 'function') { selectMarker('$selectedPinId'); }", null)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(Color(0xFF0F172A))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewClient = WebViewClient()
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    }
                    
                    // Add Bridge interface
                    addJavascriptInterface(object {
                        @Suppress("unused")
                        @JavascriptInterface
                        fun onMarkerClick(id: String) {
                            post {
                                onPinSelected(id)
                            }
                        }
                    }, "AndroidBridge")

                    webViewRef = this
                    loadDataWithBaseURL("https://api.mapbox.com/", htmlContent, "text/html", "UTF-8", null)
                }
            },
            update = { webView ->
                webViewRef = webView
            }
        )
    }
}
