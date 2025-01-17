//============================================================================
//
// Copyright (C) 2006-2023 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
//============================================================================
package org.talend.components.adapter.beam.utils;

import static java.util.Collections.emptyList;

import org.apache.beam.runners.spark.SparkContextOptions;
import org.apache.beam.runners.spark.SparkRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.talend.components.adapter.beam.BeamJobRuntimeContainer;

public class SparkRunnerTestUtils {

    private PipelineOptions options;

    private String appName;

    public SparkRunnerTestUtils(String appName) {
        this.options = PipelineOptionsFactory.create();
        this.appName = appName;
    }

    public BeamJobRuntimeContainer createRuntimeContainer() {
        return new BeamJobRuntimeContainer(options);
    }

    public Pipeline createPipeline() {
        SparkContextOptions sparkOpts = options.as(SparkContextOptions.class);
        sparkOpts.setFilesToStage(emptyList());

        SparkConf conf = new SparkConf();
        conf.setAppName(appName);
        conf.setMaster("local[2]");
        conf.set("spark.driver.allowMultipleContexts", "true");
        JavaSparkContext jsc = new JavaSparkContext(new SparkContext(conf));
        sparkOpts.setProvidedSparkContext(jsc);
        sparkOpts.setUsesProvidedSparkContext(true);
        sparkOpts.setRunner(SparkRunner.class);

        return Pipeline.create(sparkOpts);
    }
}
