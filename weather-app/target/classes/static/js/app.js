// ── Weather App Frontend ────────────────────────────

const cityInput = document.getElementById('cityInput');

cityInput.addEventListener('keydown', e => {
    if (e.key === 'Enter') searchByCity();
});

function searchByCity() {
    const city = cityInput.value.trim();
    if (!city) return;
    fetchWeather(`/api/weather/city?city=${encodeURIComponent(city)}`);
}

function useMyLocation() {
    if (!navigator.geolocation) {
        showError('Geolocation is not supported by your browser.');
        return;
    }
    showLoading();
    navigator.geolocation.getCurrentPosition(
        pos => {
            const { latitude: lat, longitude: lon } = pos.coords;
            fetchWeather(`/api/weather/coords?lat=${lat}&lon=${lon}`);
        },
        () => showError('Unable to retrieve your location.')
    );
}

async function fetchWeather(url) {
    showLoading();
    try {
        const res = await fetch(url);
        const data = await res.json();
        if (!res.ok || data.error) {
            showError(data.error || 'An error occurred.');
            return;
        }
        renderWeather(data);
    } catch (err) {
        showError('Network error. Is the server running?');
    }
}

function renderWeather(d) {
    hideAll();
    document.getElementById('resultsSection').classList.remove('hidden');

    document.getElementById('cityName').textContent = d.city || '—';
    document.getElementById('countryCode').textContent = d.country || '—';
    document.getElementById('coordsDisplay').textContent =
        `${d.lat?.toFixed(4)}° N, ${d.lon?.toFixed(4)}° E`;

    document.getElementById('temperature').textContent = Math.round(d.temperature);
    document.getElementById('feelsLike').textContent = Math.round(d.feelsLike);
    document.getElementById('tempMin').textContent = Math.round(d.tempMin);
    document.getElementById('tempMax').textContent = Math.round(d.tempMax);
    document.getElementById('weatherDesc').textContent = d.description || '—';

    // Icon
    if (d.icon) {
        document.getElementById('weatherIcon').src =
            `https://openweathermap.org/img/wn/${d.icon}@2x.png`;
    }

    // Stats
    document.getElementById('humidity').textContent = `${d.humidity}%`;
    document.getElementById('humidityBar').style.width = `${d.humidity}%`;

    document.getElementById('windSpeed').textContent = `${d.windSpeed} m/s`;
    document.getElementById('pressure').textContent = `${d.pressure} hPa`;
    document.getElementById('visibility').textContent =
        d.visibility >= 1000 ? `${(d.visibility / 1000).toFixed(1)} km` : `${d.visibility} m`;

    document.getElementById('clouds').textContent = `${d.clouds}%`;
    document.getElementById('cloudsBar').style.width = `${d.clouds}%`;

    document.getElementById('windDir').textContent = degreesToDir(d.windDeg);

    // Sun times
    const fmt = ts => new Date(ts * 1000).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    document.getElementById('sunrise').textContent = fmt(d.sunrise);
    document.getElementById('sunset').textContent = fmt(d.sunset);

    // Sun progress
    const now = Date.now() / 1000;
    const total = d.sunset - d.sunrise;
    const elapsed = Math.max(0, Math.min(now - d.sunrise, total));
    const pct = (elapsed / total) * 100;
    document.getElementById('sunProgress').style.width = `${pct}%`;
    document.getElementById('sunDot').style.left = `calc(${pct}% - 8px)`;

    // Analysis
    document.getElementById('analysisText').textContent = d.analysis || '—';
}

function degreesToDir(deg) {
    const dirs = ['N','NNE','NE','ENE','E','ESE','SE','SSE','S','SSW','SW','WSW','W','WNW','NW','NNW'];
    return dirs[Math.round(deg / 22.5) % 16];
}

function showLoading() {
    hideAll();
    document.getElementById('loadingState').classList.remove('hidden');
}

function showError(msg) {
    hideAll();
    document.getElementById('errorState').classList.remove('hidden');
    document.getElementById('errorMessage').textContent = msg;
}

function hideAll() {
    ['loadingState','errorState','resultsSection'].forEach(id =>
        document.getElementById(id).classList.add('hidden')
    );
}
