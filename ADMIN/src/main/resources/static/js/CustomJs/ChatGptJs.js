
    function removeErrorQueryParam() {
    if (window.location.search.includes("?error")) {
    const newUrl = window.location.pathname;
    history.replaceState({}, "", newUrl);
    }
    }
    // Call the function when the page is loaded
    window.addEventListener("load", removeErrorQueryParam);