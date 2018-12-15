/*
 *  (C) Copyright 2017 TheOtherP (theotherp@gmx.de)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.nzbhydra.api;

import org.nzbhydra.mapping.newznab.OutputType;
import org.nzbhydra.mapping.newznab.json.caps.*;
import org.nzbhydra.mapping.newznab.xml.caps.*;
import org.nzbhydra.update.UpdateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CapsGenerator {

    @Autowired
    private UpdateManager updateManager;

    ResponseEntity<?> getCaps(OutputType o) {
        if (o == OutputType.XML) {
            return getXmlCaps();
        } else {
            return getJsonCaps();
        }
    }

    private ResponseEntity<?> getJsonCaps() {
        CapsXmlRoot xmlCapsRoot = getXmlCapsRoot();
        CapsJsonRoot capsRoot = new CapsJsonRoot();
        capsRoot.setLimits(new CapsJsonLimits(new CapsJsonLimitsAttributes(String.valueOf(xmlCapsRoot.getLimits().getMax()), String.valueOf(xmlCapsRoot.getLimits().getDefaultValue()))));
        capsRoot.setRegistration(new CapsJsonRegistration(new CapsJsonRegistrationAttributes("no", "no")));
        CapsJsonServerAttributes serverAttributes = new CapsJsonServerAttributes();
        serverAttributes.setAppversion(updateManager.getCurrentVersionString());
        serverAttributes.setVersion(updateManager.getCurrentVersionString());
        serverAttributes.setEmail(xmlCapsRoot.getServer().getEmail());
        serverAttributes.setTitle(xmlCapsRoot.getServer().getTitle());
        serverAttributes.setUrl(xmlCapsRoot.getServer().getUrl());
        serverAttributes.setImage(xmlCapsRoot.getServer().getImage());
        capsRoot.setServer(new CapsJsonServer(serverAttributes));
        CapsJsonSearchIdAttributesHolder searchAttributes = new CapsJsonSearchIdAttributesHolder(new CapsJsonIdAttributes(xmlCapsRoot.getSearching().getSearch().getAvailable(), xmlCapsRoot.getSearching().getSearch().getSupportedParams()));
        CapsJsonSearchIdAttributesHolder tvSearchAttributes = new CapsJsonSearchIdAttributesHolder(new CapsJsonIdAttributes(xmlCapsRoot.getSearching().getTvSearch().getAvailable(), xmlCapsRoot.getSearching().getTvSearch().getSupportedParams()));
        CapsJsonSearchIdAttributesHolder movieSearchAttributes = new CapsJsonSearchIdAttributesHolder(new CapsJsonIdAttributes(xmlCapsRoot.getSearching().getMovieSearch().getAvailable(), xmlCapsRoot.getSearching().getMovieSearch().getSupportedParams()));
        CapsJsonSearchIdAttributesHolder audioSearchAttributes = new CapsJsonSearchIdAttributesHolder(new CapsJsonIdAttributes(xmlCapsRoot.getSearching().getAudioSearch().getAvailable(), xmlCapsRoot.getSearching().getAudioSearch().getSupportedParams()));
        CapsJsonSearchIdAttributesHolder bookSearchAttributes = new CapsJsonSearchIdAttributesHolder(new CapsJsonIdAttributes(xmlCapsRoot.getSearching().getBookSearch().getAvailable(), xmlCapsRoot.getSearching().getBookSearch().getSupportedParams()));
        capsRoot.setSearching(new CapsJsonSearching(searchAttributes, tvSearchAttributes, movieSearchAttributes, audioSearchAttributes, bookSearchAttributes));

        List<CapsJsonCategory> categories = new ArrayList<>();
        for (CapsXmlCategory xmlCategory : xmlCapsRoot.getCategories().getCategories()) {
            List<CapsJsonCategory> subCategories = new ArrayList<>();
            for (CapsXmlCategory xmlSubCategory : xmlCategory.getSubCategories()) {
                subCategories.add(new CapsJsonCategory(new CapsJsonCategoryAttributes(String.valueOf(xmlCategory.getId()), xmlSubCategory.getName())));
            }
            categories.add(new CapsJsonCategory(new CapsJsonCategoryAttributes(String.valueOf(xmlCategory.getId()), xmlCategory.getName()), subCategories));
        }

        capsRoot.setCategories(new CapsJsonCategoriesHolder(categories));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(capsRoot, headers, HttpStatus.OK);
    }

    private ResponseEntity<?> getXmlCaps() {
        CapsXmlRoot capsRoot = getXmlCapsRoot();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        return new ResponseEntity<>(capsRoot, headers, HttpStatus.OK);
    }

    private CapsXmlRoot getXmlCapsRoot() {
        CapsXmlRoot capsRoot = new CapsXmlRoot();
        capsRoot.setRetention(new CapsXmlRetention(3000));
        capsRoot.setLimits(new CapsXmlLimits(100, 100)); //later link to global setting when implemented

        CapsXmlServer capsServer = new CapsXmlServer();
        capsServer.setEmail("theotherp@gmx.de");
        capsServer.setTitle("NZBHydra 2");
        capsServer.setUrl("https://github.com/theotherp/nzbhydra2");
        capsServer.setImage("https://raw.githubusercontent.com/theotherp/nzbhydra2/master/core/ui-src/img/banner-bright.png");
        capsRoot.setServer(capsServer);

        CapsXmlSearching capsSearching = new CapsXmlSearching();
        capsSearching.setSearch(new CapsXmlSearch("yes", "q,cat,limit,offset,minage,maxage,minsize,maxsize"));
        capsSearching.setTvSearch(new CapsXmlSearch("yes", "q,rid,tvdbid,tvmazeid,traktid,season,ep,cat,limit,offset,minage,maxage,minsize,maxsize"));
        capsSearching.setMovieSearch(new CapsXmlSearch("yes", "q,imdbid,tmdbid,cat,limit,offset,minage,maxage,minsize,maxsize"));
        capsSearching.setBookSearch(new CapsXmlSearch("yes", "q,author,title,cat,limit,offset,minage,maxage,minsize,maxsize"));
        capsSearching.setAudioSearch(new CapsXmlSearch("no", ""));
        capsRoot.setSearching(capsSearching);

        List<CapsXmlCategory> mainCategories = new ArrayList<>();
        mainCategories.add(new CapsXmlCategory(1000, "Console", Arrays.asList(
                new CapsXmlCategory(1010, "NDS"),
                new CapsXmlCategory(1020, "PSP"),
                new CapsXmlCategory(1030, "Wii"),
                new CapsXmlCategory(1040, "XBox"),
                new CapsXmlCategory(1050, "Xbox 360"),
                new CapsXmlCategory(1060, "Wiiware"),
                new CapsXmlCategory(1070, "Xbox 360 DLC")
        )));
        mainCategories.add(new CapsXmlCategory(2000, "Movies", Arrays.asList(
                new CapsXmlCategory(2010, "Foreign"),
                new CapsXmlCategory(2020, "Other"),
                new CapsXmlCategory(2030, "SD"),
                new CapsXmlCategory(2040, "HD"),
                new CapsXmlCategory(2045, "UHD"),
                new CapsXmlCategory(2050, "Bluray"),
                new CapsXmlCategory(2060, "3D")
        )));
        mainCategories.add(new CapsXmlCategory(3000, "Audio", Arrays.asList(
                new CapsXmlCategory(3010, "MP3"),
                new CapsXmlCategory(3020, "Video"),
                new CapsXmlCategory(3030, "Audiobook"),
                new CapsXmlCategory(3040, "Lossless")
        )));
        mainCategories.add(new CapsXmlCategory(4000, "PC", Arrays.asList(
                new CapsXmlCategory(4010, "0day"),
                new CapsXmlCategory(4020, "ISO"),
                new CapsXmlCategory(4030, "Mac"),
                new CapsXmlCategory(4040, "Mobile Oher"),
                new CapsXmlCategory(4050, "Games"),
                new CapsXmlCategory(4060, "Mobile IOS"),
                new CapsXmlCategory(4070, "Mobile Android")
        )));
        mainCategories.add(new CapsXmlCategory(5000, "TV", Arrays.asList(
                new CapsXmlCategory(5020, "Foreign"),
                new CapsXmlCategory(5030, "SD"),
                new CapsXmlCategory(5040, "HD"),
                new CapsXmlCategory(5045, "UHD"),
                new CapsXmlCategory(5050, "Other"),
                new CapsXmlCategory(5060, "Sport"),
                new CapsXmlCategory(5070, "Anime"),
                new CapsXmlCategory(5080, "Documentary")
        )));
        mainCategories.add(new CapsXmlCategory(6000, "XXX", Arrays.asList(
                new CapsXmlCategory(6010, "DVD"),
                new CapsXmlCategory(6020, "WMV"),
                new CapsXmlCategory(6030, "XviD"),
                new CapsXmlCategory(6040, "x264"),
                new CapsXmlCategory(6050, "Pack"),
                new CapsXmlCategory(6060, "Imgset"),
                new CapsXmlCategory(6070, "Other")
        )));
        mainCategories.add(new CapsXmlCategory(7000, "Books", Arrays.asList(
                new CapsXmlCategory(7010, "Mags"),
                new CapsXmlCategory(7020, "Ebook"),
                new CapsXmlCategory(7030, "COmics")
        )));
        mainCategories.add(new CapsXmlCategory(8000, "Other", Arrays.asList(
                new CapsXmlCategory(8010, "Misc")
        )));


        capsRoot.setCategories(new CapsXmlCategories(mainCategories));
        return capsRoot;
    }
}