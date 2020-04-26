/*
 * Copyright 2020 Veronica Anokhina.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

window.browser = window.browser || window.chrome;
window.chrome = window.browser;
var port = "7788";

chrome.runtime.onInstalled.addListener(function () {
    console.log("INSTALLED");

    chrome.contextMenus.create({
        id: "localres-search-selected",
        title: "Go to local selection: %s",
        contexts: ["selection"]
    });

    chrome.contextMenus.create({
        id: "localres-open-link",
        title: "Go to local url: %s",
        contexts: ["link"]
    });

});

chrome.contextMenus.onClicked.addListener(function (info, tab) {
	//console.log(info);
	if (info.menuItemId == "localres-search-selected") {
	    //alert('Search for ' + info.selectionText);
	    chrome.tabs.create({
	        //url: encodeURI("http://www.google.com/search?q=" + info.selectionText)
	        url: encodeURI("http://127.0.0.1:" + port + "/dir?path=" + info.selectionText)
	    });
	}
	if (info.menuItemId == "localres-open-link") {
	    //alert('Open for ' + info.linkUrl);
	    try {
	        chrome.tabs.create({
	            url: encodeURI("http://127.0.0.1:" + port + "/dir?path=" + info.linkUrl)
	        });
	    } catch (e) {
	        chrome.tabs.create({
	            url: chrome.extension.getURL("error.html")
	        });
	    }
	}
});


