package com.coxautodata.vegalite4s.spark.filestore

import org.apache.hadoop.fs.Path
import org.scalatest.{FunSpec, Matchers}

class DatabricksFileStoreSpec extends FunSpec with Matchers {

  it("generate a new working directory for each call"){

    DatabricksFileStore.generateOutputPath() should not be DatabricksFileStore.generateOutputPath()

  }

  it("generate a relative url pointing to a databricks filestore"){

    DatabricksFileStore.getStaticURL(new Path("dbfs:/FileStore/test/test.arrow")) should be ("files/test/test.arrow")

  }

}
